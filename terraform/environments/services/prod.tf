module "ec2_instance" {
  source = "terraform-aws-modules/ec2-instance/aws"

  name = "prod"

  instance_type          = "t2.micro"
  key_name               = aws_key_pair.prod.id
  monitoring             = true
  vpc_security_group_ids = [aws_security_group.backend.id]
  subnet_id              = data.terraform_remote_state.global.outputs.public_subnet_id
}

resource "aws_key_pair" "prod" {
  public_key = ""
}

import {
  to = aws_key_pair.prod
  id = var.key_pair_prod
}
