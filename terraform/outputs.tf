output "db_endpoint" {
  description = "Endpoint de la base de datos RDS"
  value       = aws_db_instance.franquicias_db.endpoint
}

output "app_url" {
  description = "URL de la aplicación"
  value       = aws_elastic_beanstalk_environment.franquicias_env.endpoint_url
}
