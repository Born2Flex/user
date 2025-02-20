include "root"{
  path = find_in_parent_folders("common.hcl")
}

terraform {
  source = "../../modules/subnet"
}

dependency "vpc" {
  config_path = "../vpc"

  mock_outputs = {
    vpc_id = "mock-id"
  }

  # mock_outputs_allowed_terraform_commands = ["plan"]
}

inputs = {
  vpc_id = dependency.vpc.outputs.vpc_id
  cidr_block = "10.0.2.0/24"
  availability_zone_id = "euc1-az2"
  name = "TerragruntSubnet"
}
