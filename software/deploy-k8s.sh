aws eks --region ${AWS_REGION} update-kubeconfig --name kafka-on-eks
export AWS_EVENTBRIDGE_ARN="$(aws events describe-event-bus --name "care-plan-events" --query 'Arn' --output text)"
export AWS_ECR_REGISTRY="$(aws sts get-caller-identity --query 'Account' --output text).dkr.ecr.${AWS_REGION}.amazonaws.com"

envsubst < k8s/care-plan.yaml | kubectl apply -f -
envsubst < k8s/provider-schedule.yaml | kubectl apply -f -
envsubst < k8s/insurance-processor.yaml | kubectl apply -f -
envsubst < k8s/connector.yaml | kubectl apply -f -

kubectl apply -f k8s/ingress.yaml