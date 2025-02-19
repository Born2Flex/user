resource "aws_security_group" "security_group" {
  name   = "AppSecGroup"
  vpc_id = var.vpc_id
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