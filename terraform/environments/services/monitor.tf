resource "aws_instance" "monitor" {
  ami           = "ami-0e6f2b2fa0ca704d0"
  instance_type = "t2.micro"
  tags          = {
    Name = "monitor"
  }
}

import {
  to = aws_instance.monitor
  id = var.instance_id_monitor
}
