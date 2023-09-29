#---------------------------------------------------------------
# External Secrets - Cluster Secrets Store for communication with AWS Secrets Manager
#---------------------------------------------------------------

locals {
  secret_namespace                = "external-secrets"
  cluster_secretstore_name = "cluster-secretstore-sm"
  cluster_secretstore_sa   = "cluster-secretstore-sa"
  secretstore_name         = "secretstore-ps"
  secretstore_sa           = "secretstore-sa"
}

resource "aws_iam_policy" "cluster_secretstore" {
  name_prefix = local.cluster_secretstore_sa
  policy      = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "secretsmanager:GetResourcePolicy",
        "secretsmanager:GetSecretValue",
        "secretsmanager:DescribeSecret",
        "secretsmanager:ListSecretVersionIds"
      ],
      "Resource": "arn:aws:secretsmanager:*:*:secret:*"
    }
  ]
}
POLICY
}

module "cluster_secretstore_role" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-role-for-service-accounts-eks"
  version = "~> 5.20"

  role_name_prefix = "${module.eks.cluster_name}-secrets-manager-"

  role_policy_arns = {
    policy = aws_iam_policy.cluster_secretstore.arn
  }

  oidc_providers = {
    main = {
      provider_arn               = module.eks.oidc_provider_arn
      namespace_service_accounts = ["${local.secret_namespace}:${local.cluster_secretstore_sa}"]
    }
  }

  tags = local.tags
}


resource "helm_release" "cluster_secretstore" {
  name       = "cluster-secretstore"
  chart = "${path.module}/cluster-secret-store"
  namespace  = local.secret_namespace
  version = "1.0.1"

  values = [templatefile("${path.module}/cluster-secret-store/values.yaml", {
    aws_region = local.region,
    cluster_secret_store_sa = local.cluster_secretstore_sa,
    cluster_secret_store_name = local.cluster_secretstore_name,
    secret_namespace = local.secret_namespace
    cluster_secrets_store_iam_role_arn = module.cluster_secretstore_role.iam_role_arn
  })]
}
