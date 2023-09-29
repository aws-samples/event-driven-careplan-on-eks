locals {
  app_namespace = "app"
}

module "ecr" {
  source = "terraform-aws-modules/ecr/aws"

  registry_scan_rules = [
    {
      scan_frequency = "SCAN_ON_PUSH"
      filter         = "*"
      filter_type    = "WILDCARD"
    }
  ]

  repository_name = "managed-care-plan-registry"
  repository_image_tag_mutability = "MUTABLE"
  repository_read_write_access_arns = [data.aws_caller_identity.current.arn]
  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1,
        description  = "Keep last 30 images",
        selection = {
          tagStatus     = "tagged",
          tagPrefixList = ["v"],
          countType     = "imageCountMoreThan",
          countNumber   = 30
        },
        action = {
          type = "expire"
        }
      }
    ]
  })
  repository_force_delete = true
}

resource "helm_release" "app_environment" {

  name       = "app-environment"
  chart = "${path.module}/app-environment"
  version = "1.2.0"

  values = [templatefile("${path.module}/app-environment/values.yaml", {
    app_namespace = local.app_namespace
    rds_jdbc_url = local.rds_jdbc_url
    cluster_secret_store_name = local.cluster_secretstore_name,
    aws_secrets_manager_secret_rds_secret_name = data.aws_secretsmanager_secret.rds_secret.name
    aws_secrets_manager_secret_api_key_name = aws_secretsmanager_secret.managed_care_plan_api_key.name
    ecr_repository_url = module.ecr.repository_url
    ecr_secret_name = local.ecr_secret_name
    eventbridge_kafka_connect_role_iam_role_arn = module.evenbridge_kafka_connect_role.iam_role_arn
  })]

  depends_on = [helm_release.cluster_secretstore]
}



# Event bridge resources
resource "aws_cloudwatch_event_bus" "care_plan_events" {
  name = "care-plan-events"
}

# all confirmed events
resource "aws_cloudwatch_event_rule" "all_confirmed_care_plan_events" {
  name           = "all-confirmed-care-plan-events"
  description    = "Capture all care plan confirmed events for processing"
  event_bus_name = aws_cloudwatch_event_bus.care_plan_events.name

  event_pattern = jsonencode({
    "region" : [local.region],
    "detail" : {
      "value" : {
        "booking" : {
          "status" : ["CONFIRMED"]
        }
      }
    }

  })
}

resource "aws_cloudwatch_event_target" "confirmed_events_sqs" {
  rule           = aws_cloudwatch_event_rule.all_confirmed_care_plan_events.name
  target_id      = "SendToSQSForConfirmedCarePlanEvents"
  arn            = aws_sqs_queue.confirmed_care_plan_events_queue.arn
  event_bus_name = aws_cloudwatch_event_bus.care_plan_events.name
  input_transformer {
    input_paths = {
      "patientId" : "$.detail.value.patient.id",
      "bookingId" : "$.detail.value.booking.id",
      "eventId" : "$.id"
    }
    input_template = <<EOF
    {
      "source":"com.providerschedule",

      "event-type": "Notification",
      "id" : "<eventId>",
      "data":{
        "patientId" : "<patientId>",
        "bookingId" : "<bookingId>"
      }
    }
    EOF


  }
}

resource "aws_sqs_queue" "confirmed_care_plan_events_queue" {
  name = "confirmed-care-plan-events-queue"
  sqs_managed_sse_enabled = true
}


# all in person events
resource "aws_cloudwatch_event_rule" "onsite_confirmed_care_plan_events" {
  name           = "onsite-confirmed-care-plan-events"
  description    = "Capture all onsite care plan confirmed events for processing"
  event_bus_name = aws_cloudwatch_event_bus.care_plan_events.name

  event_pattern = jsonencode({
    "region" : [local.region],
    "detail" : {
      "value" : {
        "encounterType" : ["ONSITE"]
      }
    }
  })
}



resource "aws_sqs_queue_policy" "eb_sqs_put_event_policy_for_all_confirmed" {
  queue_url = aws_sqs_queue.confirmed_care_plan_events_queue.url
  policy    = data.aws_iam_policy_document.eb_sqs_put_event_policy_doc_confirmed.json

}
data "aws_iam_policy_document" "eb_sqs_put_event_policy_doc_confirmed" {

  statement {
    effect  = "Allow"
    actions = ["sqs:*"]

    principals {
      type        = "Service"
      identifiers = ["events.amazonaws.com"]
    }

    resources = [aws_sqs_queue.confirmed_care_plan_events_queue.arn]
  }
}



locals {
  kafka_connect_sa = "connect-cluster-connect"
}

resource "aws_iam_policy" "eventbridge_connect_iam" {
  name_prefix = local.kafka_connect_sa
  policy      = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "events:PutEvents"
      ],
      "Resource": "${aws_cloudwatch_event_bus.care_plan_events.arn}"
    }
  ]
}
POLICY
}

module "evenbridge_kafka_connect_role" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.20"

  role_name_prefix = "${module.eks.cluster_name}-kafka-connect-"

  role_policy_arns = {
    policy = aws_iam_policy.eventbridge_connect_iam.arn
  }

  oidc_providers = {
    main = {
      provider_arn               = module.eks.oidc_provider_arn
      namespace_service_accounts = ["${local.kafka_namespace}:${local.kafka_connect_sa}"]
    }
  }
}

data "aws_ecr_authorization_token" "token" {}

locals {
  ecr_secret_name = "docker-ecr-config"
}

resource "kubernetes_secret" "docker" {
  depends_on = [module.eks_data_addons]
  metadata {
    name      = local.ecr_secret_name
    namespace = local.kafka_namespace
  }

  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        "${data.aws_ecr_authorization_token.token.proxy_endpoint}" = {
          auth = "${data.aws_ecr_authorization_token.token.authorization_token}"
        }
      }
    })
  }
  type = "kubernetes.io/dockerconfigjson"
}

# Lambda function resources for triggering clinic operations
module "onsite_events_lambda_function" {

  source        = "terraform-aws-modules/lambda/aws"
  function_name = "onsite-events-lambda"
  description   = "Confirmed onsite events lambda function"
  handler       = "app.lambda_handler"
  runtime       = "python3.10"

  source_path = "../../software/insurance-processor/src/main/python"

  tags = {
    Name = "my-lambda1"
  }
}

resource "aws_cloudwatch_event_target" "onsite_confirmed_events_lambda" {
  rule           = aws_cloudwatch_event_rule.onsite_confirmed_care_plan_events.name
  target_id      = "SendToLambdaForConfirmedCarePlanEvents"
  arn            = module.onsite_events_lambda_function.lambda_function_arn
  event_bus_name = aws_cloudwatch_event_bus.care_plan_events.name
}

resource "aws_lambda_permission" "allow_cloudwatch_to_call_lambda" {
  statement_id = "AllowExecutionFromCloudWatch"
  action = "lambda:InvokeFunction"
  function_name = module.onsite_events_lambda_function.lambda_function_name
  principal = "events.amazonaws.com"
  source_arn = aws_cloudwatch_event_rule.onsite_confirmed_care_plan_events.arn
}

resource "aws_secretsmanager_secret" "managed_care_plan_api_key" {
  name = "managed-care-plan-api-key"
  recovery_window_in_days = 0
}

data "aws_secretsmanager_random_password" "managed_care_plan_api_key" {
  password_length = 30
}

resource "aws_secretsmanager_secret_version" "this" {
  secret_id = aws_secretsmanager_secret.managed_care_plan_api_key.id
  secret_string = jsonencode({
    apiKey = data.aws_secretsmanager_random_password.managed_care_plan_api_key.random_password
  })
}
