locals {
  rds_port = 5432
  rds_db_name = "managed_care_plan_db"
  rds_db_username = "root"
  rds_jdbc_url = format("jdbc:postgresql://%s/%s", module.rds.db_instance_endpoint, local.rds_db_name)
}

module "rds" {
  source = "terraform-aws-modules/rds/aws"
  identifier = "managed-care-plan-db"

  engine            = "postgres"
  engine_version    = "14"
  instance_class    = "db.t4g.large"
  allocated_storage = 20

  db_name  = local.rds_db_name
  username = local.rds_db_username
  port     = local.rds_port

  vpc_security_group_ids = [module.security_group.security_group_id]

  maintenance_window = "Mon:00:00-Mon:03:00"
  # Disable backups for demo purposes
  backup_retention_period = 0

  # DB subnet group
  create_db_subnet_group = true
  subnet_ids             = module.vpc.private_subnets

  # DB parameter group
  create_db_parameter_group = false

  # DB option group
  create_db_option_group = false

  # Database Deletion Protection
  deletion_protection = false
}
data "aws_secretsmanager_secret" "rds_secret" {
  arn = module.rds.db_instance_master_user_secret_arn
}

module "security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.0"

  name        = local.name
  description = "PostgreSQL security group"
  vpc_id      = module.vpc.vpc_id

  # ingress
  ingress_with_cidr_blocks = [
    {
      from_port   = local.rds_port
      to_port     = local.rds_port
      protocol    = "tcp"
      description = "PostgreSQL access from within VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
  ]

  tags = local.tags
}
