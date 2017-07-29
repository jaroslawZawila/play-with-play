resource "aws_dynamodb_table" "customers" {
  name = "customers"
  read_capacity = 2
  write_capacity = 2
  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags {
    Name = "customers"
    Environment = "test"
  }
}