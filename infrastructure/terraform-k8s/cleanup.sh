#!/bin/bash
set -o errexit
set -o pipefail

region=${AWS_REGION}
echo "Cleaning up modules in AWS region: $region"

targets=(
  "module.cluster_secretstore_role"
  "module.eks_data_addons"
  "module.eks_blueprints_addons"
)

for target in "${targets[@]}"
do
  terraform destroy -target="$target" -auto-approve -var="region=$region"
  destroy_output=$(terraform destroy -target="$target" -auto-approve -var="region=$region" 2>&1)
  if [[ $? -eq 0 && $destroy_output == *"Destroy complete!"* ]]; then
    echo "SUCCESS: Terraform destroy of $target completed successfully"
  else
    echo "FAILED: Terraform destroy of $target failed"
    exit 1
  fi
done

terraform destroy -auto-approve -var="region=$region"
destroy_output=$(terraform destroy -auto-approve -var="region=$region" 2>&1)
if [[ $? -eq 0 && $destroy_output == *"Destroy complete!"* ]]; then
  echo "SUCCESS: Terraform destroy of all targets completed successfully"
else
  echo "FAILED: Terraform destroy of all targets failed"
  exit 1
fi
