#!/bin/bash

echo "Initializing Terraform Modules"
terraform init

region=${AWS_REGION}
echo "Deploying modules to AWS region: $region"

# List of Terraform modules to apply in sequence
targets=(
  "module.vpc"
  "module.eks"
  "module.ebs_csi_driver_irsa"
  "module.eks_blueprints_addons"
  "module.eks_data_addons"
  "module.cluster_secretstore_role"
)

# Apply modules in sequence
for target in "${targets[@]}"
do
  echo "Applying module $target..."
  apply_output=$(terraform apply -target="$target" -auto-approve -var="region=$region" 2>&1 | tee /dev/tty)
  if [[ ${PIPESTATUS[0]} -eq 0 && $apply_output == *"Apply complete"* ]]; then
    echo "SUCCESS: Terraform apply of $target completed successfully"
  else
    echo "FAILED: Terraform apply of $target failed"
    exit 1
  fi
done

# Final apply to catch any remaining resources
echo "Applying remaining resources..."
apply_output=$(terraform apply -auto-approve -var="region=$region" 2>&1 | tee /dev/tty)
if [[ ${PIPESTATUS[0]} -eq 0 && $apply_output == *"Apply complete"* ]]; then
  echo "SUCCESS: Terraform apply of all modules completed successfully"
else
  echo "FAILED: Terraform apply of all modules failed"
  exit 1
fi
