resource "aws_vpc" "terra_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name = "TerraVPC"
  }
}

module "public_subnet_1" {
  source = "../subnet"

  vpc_id               = aws_vpc.terra_vpc.id
  availability_zone_id = "euc1-az2"
  cidr_block           = "10.0.2.0/24"
  name                 = "PublicSubnet1"
}

module "public_subnet_2" {
  source = "../subnet"

  vpc_id               = aws_vpc.terra_vpc.id
  availability_zone_id = "euc1-az3"
  cidr_block           = "10.0.3.0/24"
  name                 = "PublicSubnet2"
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
  subnet_id      = module.public_subnet_1.subnet_id
}

resource "aws_route_table_association" "route_table_association_2" {
  route_table_id = aws_route_table.public_subnet_route_table.id
  subnet_id      = module.public_subnet_2.subnet_id
}
