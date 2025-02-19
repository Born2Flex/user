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

resource "aws_vpc" "terra_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "TerraVPC"
  }
}

resource "aws_subnet" "public_subnet" {
  vpc_id               = aws_vpc.terra_vpc.id
  cidr_block           = "10.0.2.0/24"
  availability_zone_id = "euc1-az2"

  tags = {
    Name = "PublicSubnet"
  }
}

resource "aws_subnet" "public_subnet_2" {
  vpc_id               = aws_vpc.terra_vpc.id
  cidr_block           = "10.0.3.0/24"
  availability_zone_id = "euc1-az3"

  tags = {
    Name = "PublicSubnet2"
  }
}

resource "aws_internet_gateway" "terra_internet_gateway" {
  vpc_id = aws_vpc.terra_vpc.id

  tags = {
    Name = "TerraInternetGateway"
  }
}

resource "aws_route_table" "public_subnet_route_table" {
  vpc_id = aws_vpc.terra_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.terra_internet_gateway.id
  }

  tags = {
    Name = "PublicSubnetRouteTable"
  }
}

resource "aws_route_table_association" "route_table_association" {
  route_table_id = aws_route_table.public_subnet_route_table.id
  subnet_id      = aws_subnet.public_subnet.id
}

resource "aws_route_table_association" "route_table_association_2" {
  route_table_id = aws_route_table.public_subnet_route_table.id
  subnet_id      = aws_subnet.public_subnet_2.id
}

resource "aws_security_group" "security_group" {
  name   = "AppSecGroup"
  vpc_id = aws_vpc.terra_vpc.id
}

resource "aws_security_group_rule" "allow_ssh" {
  security_group_id = aws_security_group.security_group.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 22
  to_port           = 22
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow_db_connect" {
  security_group_id = aws_security_group.security_group.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 5432
  to_port           = 5432
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "activemq_ui" {
  security_group_id = aws_security_group.security_group.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 8162
  to_port           = 8162
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "activemq_broker_connection" {
  security_group_id = aws_security_group.security_group.id
  type              = "ingress"
  protocol          = "tcp"
  from_port         = 61617
  to_port           = 61617
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow_all" {
  security_group_id = aws_security_group.security_group.id
  type              = "egress"
  protocol          = -1
  from_port         = 0
  to_port           = 0
  cidr_blocks = ["0.0.0.0/0"]
}

resource "aws_instance" "application_instance" {

  ami                         = "ami-0084a47cc718c111a"
  instance_type               = var.ec2_instance_type
  subnet_id                   = aws_subnet.public_subnet.id
  vpc_security_group_ids = [aws_security_group.security_group.id]
  associate_public_ip_address = true

  tags = {
    Name = var.instance_name
  }
}


resource "aws_db_subnet_group" "database_subnet" {
  subnet_ids = [aws_subnet.public_subnet.id, aws_subnet.public_subnet_2.id]
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
  vpc_security_group_ids = [aws_security_group.security_group.id]
}

resource "aws_mq_broker" "activemq_instance" {
  broker_name                = "TerraBroker"
  engine_type                = "ActiveMQ"
  engine_version             = "5.18"
  host_instance_type         = "mq.t2.micro"
  security_groups = [aws_security_group.security_group.id]
  auto_minor_version_upgrade = true
  publicly_accessible        = true
  subnet_ids = [aws_subnet.public_subnet.id]
  apply_immediately          = true

  user {
    username       = "user"
    password       = "Ml01smX1j0am"
    console_access = true
  }
}