###################
# Version
###################
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.3"
    }
  }
  required_version = ">= 0.13"
}

provider "aws" {
  region  = "ap-northeast-2"
  profile = ""
}

###################
# VPC
###################
resource "aws_vpc" "this" {
  cidr_block = ""
}

//public subnet
resource "aws_subnet" "public" {
  vpc_id                  = ""
  cidr_block              = ""
  availability_zone       = "ap-northeast-2a"
  map_public_ip_on_launch = true
}

resource "aws_internet_gateway" "this" {
  vpc_id = ""
}

resource "aws_default_route_table" "public_rt" {
  default_route_table_id = ""

  route {
    cidr_block = ""
    gateway_id = ""
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = ""
  route_table_id = ""
}

//private subnet
resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.this.id
  cidr_block        = "10.0.20.0/24"
  availability_zone = "ap-northeast-2a"
}

resource "aws_eip" "nat" {
  domain = "vpc"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_nat_gateway" "this" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public.id
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.this.id
}

resource "aws_route_table_association" "private" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.private.id
}

resource "aws_route" "private" {
  route_table_id         = aws_route_table.private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.this.id
}

###################
# EC2
###################
resource "aws_instance" "dev" {
  ami                         = "ami-02c956980e9e063e5"
  instance_type               = "t2.small"
  associate_public_ip_address = true
  key_name                    = ""
  subnet_id                   = ""
  vpc_security_group_ids      = [""]
  iam_instance_profile        = ""
}

#################
# Security Group
#################
resource "aws_security_group" "this" {
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

###########
# IAM Prod
###########
resource "aws_iam_instance_profile" "this" {
  role = aws_iam_role.ec2.name
}

resource "aws_iam_role" "ec2" {
  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}


