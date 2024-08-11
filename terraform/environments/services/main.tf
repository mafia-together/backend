###########
# vpc
###########
resource "aws_vpc" "this" {
  cidr_block = "172.31.0.0/16"
}

import {
  to = aws_vpc.this
  id = var.vpc_id
}

#################
# public subnet
#################
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.this.id
  cidr_block              = "172.31.100.0/24"
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true
}

resource "aws_internet_gateway" "this" {
}

import {
  to = aws_internet_gateway.this
  id = var.ig_id
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route" "side_effect_internet_access" {
  route_table_id         = aws_route_table.public.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.this.id
}

#################
# private subnet
#################
resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.this.id
  cidr_block        = "172.31.101.0/24"
  availability_zone = "ap-northeast-2a"
}

###########
# prod
###########


###########
# dev
###########
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

###########
# redis
###########
resource "aws_instance" "redis" {
  ami           = "ami-0e6f2b2fa0ca704d0"
  instance_type = "t2.micro"
  tags          = {
    Name = "redis"
  }
}

import {
  to = aws_instance.redis
  id = var.instance_id_redis
}

###########
# monitor
###########
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

###########
# jenkins
###########
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

###########
# sg
###########
resource "aws_security_group" "backend" {
  name   = var.project_name
  vpc_id = aws_vpc.this.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

##################
# terraform_state
##################
resource "aws_s3_bucket" "state" {
  bucket        = var.bucket_name
  force_destroy = true
}

resource "aws_s3_bucket_versioning" "versioning" {
  bucket = aws_s3_bucket.state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_dynamodb_table" "terraform_state_lock" {
  name         = var.project_name
  hash_key     = "LockID"
  billing_mode = "PAY_PER_REQUEST"

  attribute {
    name = "LockID"
    type = "S"
  }
}
