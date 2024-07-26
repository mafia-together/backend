resource "aws_instance" "prod" {
  ami           = "ami-0e6f2b2fa0ca704d0"
  instance_type = "t2.micro"
  tags          = {
    Name = "dev"
  }
}

import {
  to = aws_instance.prod
  id = var.instance_id_dev
}
