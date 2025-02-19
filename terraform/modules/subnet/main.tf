resource "aws_subnet" "public_subnet" {
  vpc_id = var.vpc_id
  cidr_block = var.cidr_block
  availability_zone_id = var.availability_zone_id

  tags = {
    Name = var.name
  }
}