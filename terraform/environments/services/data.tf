data "terraform_remote_state" "global" {
  backend = "local"
  config  = {
    path = "../global/terraform.tfstate"
  }
}
