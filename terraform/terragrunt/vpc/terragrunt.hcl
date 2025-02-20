include "root"{
  path = find_in_parent_folders("common.hcl")
}

terraform {
  source = "../../modules/terragrunt_vpc"
}

inputs = {
  cidr_block = "10.0.0.0/16"
  name = "TerragruntVPC"
}