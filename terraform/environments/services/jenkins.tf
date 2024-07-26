resource "aws_instance" "jenkins" {
  ami           = "ami-0e6f2b2fa0ca704d0"
  instance_type = "t2.micro"
  tags          = {
    Name = "jenkins"
  }
}

import {
  to = aws_instance.jenkins
  id = var.instance_id_jenkins
}
