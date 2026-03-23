terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Security Group para RDS
resource "aws_security_group" "rds_sg" {
  name        = "franquicias-rds-sg"
  description = "Permite acceso MySQL"

  ingress {
    from_port   = 3306
    to_port     = 3306
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

# Base de datos RDS MySQL
resource "aws_db_instance" "franquicias_db" {
  identifier             = "franquicias-db"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = "franquiciasdb"
  username               = var.db_username
  password               = var.db_password
  skip_final_snapshot    = true
  publicly_accessible    = true
  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  tags = {
    Name = "franquicias-db"
  }
}

# Elastic Beanstalk Application
resource "aws_elastic_beanstalk_application" "franquicias_app" {
  name        = "franquicias-api"
  description = "API de franquicias Spring Boot"
}

# Elastic Beanstalk Environment
resource "aws_elastic_beanstalk_environment" "franquicias_env" {
  name                = "franquicias-api-env"
  application         = aws_elastic_beanstalk_application.franquicias_app.name
  solution_stack_name = "64bit Amazon Linux 2 v3.4.0 running Corretto 17"

  setting {
    namespace = "aws:autoscaling:launchconfiguration"
    name      = "InstanceType"
    value     = "t3.micro"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_URL"
    value     = "jdbc:mysql://${aws_db_instance.franquicias_db.endpoint}/franquiciasdb"
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_USERNAME"
    value     = var.db_username
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_DATASOURCE_PASSWORD"
    value     = var.db_password
  }

  setting {
    namespace = "aws:elasticbeanstalk:application:environment"
    name      = "SPRING_PROFILES_ACTIVE"
    value     = "docker"
  }
}
