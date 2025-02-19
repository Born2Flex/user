output "subnet1_id" {
  value = module.public_subnet_1.subnet_id
}

output "subnet2_id" {
  value = module.public_subnet_2.subnet_id
}

output "vpc_id" {
  value = aws_vpc.terra_vpc.id
}
