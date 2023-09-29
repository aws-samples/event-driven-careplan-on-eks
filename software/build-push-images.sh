#Login to Private ECR Registry and Public ECR for Corretto Images
export AWS_ECR_REGISTRY="$(aws sts get-caller-identity --query 'Account' --output text).dkr.ecr.${AWS_REGION}.amazonaws.com"
aws ecr get-login-password | docker login -u AWS --password-stdin ${AWS_ECR_REGISTRY}

#Build all application images from Docker Compose File
docker compose -f builder.yml build

#Push each file individually to re-use existing layers
docker push $AWS_ECR_REGISTRY/managed-care-plan-registry:care-plan-1.2
docker push $AWS_ECR_REGISTRY/managed-care-plan-registry:provider-schedule-1.2
docker push $AWS_ECR_REGISTRY/managed-care-plan-registry:insurance-processor-1.2