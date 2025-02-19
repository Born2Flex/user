terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.54.1"
    }
  }
}

provider "aws" {
  profile = "default"
  region  = "eu-central-1"
}

module "terra_vpc" {
  source = "./modules/vpc"
}

module "security_group" {
  source = "./modules/security_group"

  vpc_id = module.terra_vpc.vpc_id
}

module "ec2_instance" {
  source = "./modules/compute"

  ami               = "ami-0084a47cc718c111a"
  ec2_instance_type = "t2.micro"
  instance_name     = "TerraInstanceName"
  public_subnet_id  = module.terra_vpc.subnet1_id
  security_group_id = module.security_group.id
}

resource "aws_db_subnet_group" "database_subnet" {
  subnet_ids = [module.terra_vpc.subnet1_id, module.terra_vpc.subnet2_id]
}

resource "aws_db_instance" "postgres_instance" {
  identifier           = "terra-database"
  allocated_storage    = 5
  storage_type         = "standard"
  engine               = "postgres"
  engine_version       = "17.2"
  instance_class       = "db.t4g.micro"
  username             = "postgres"
  password             = "password"
  skip_final_snapshot  = true
  multi_az             = false
  publicly_accessible  = true
  db_subnet_group_name = aws_db_subnet_group.database_subnet.name
  vpc_security_group_ids = [module.security_group.id]
}

resource "aws_mq_broker" "activemq_instance" {
  broker_name                = "TerraBroker"
  engine_type                = "ActiveMQ"
  engine_version             = "5.18"
  host_instance_type         = "mq.t2.micro"
  security_groups = [module.security_group.id]
  auto_minor_version_upgrade = true
  publicly_accessible        = true
  subnet_ids = [module.terra_vpc.subnet1_id]
  apply_immediately          = true

  user {
    username       = "user"
    password       = "Ml01smX1j0am"
    console_access = true
  }
}