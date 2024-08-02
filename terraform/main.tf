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

provider "aws"{
  region = "ap-northeast-2"
}
