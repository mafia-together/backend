terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.30"
    }
  }
  backend s3 {
    bucket         = "mafia-together-default-20240812"
    key            = "terraform/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "mafia-together-default"
    profile        = "mafia"
  }
}

provider "aws" {
  region  = "ap-northeast-2"
  profile = var.profile
}
