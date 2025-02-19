variable "instance_name" {
  description = "Value of the Name tag in EC2 instance"
  type = string
  default = "MyDefaultInstanceName"
}

variable "ec2_instance_type" {
  description = "Default EC2 instance type"
  type = string
  default = "t2.micro"
}