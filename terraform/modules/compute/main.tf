resource "aws_instance" "application_instance" {
  ami                         = var.ami
  instance_type               = var.ec2_instance_type
  subnet_id                   = var.public_subnet_id
  vpc_security_group_ids = [var.security_group_id]
  associate_public_ip_address = true

  tags = {
    Name = var.instance_name
  }
}