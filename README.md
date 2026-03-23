# Franquicia API

API REST desarrollada en Spring Boot para gestionar franquicias, sucursales y productos.

## App desplegada

La aplicación está corriendo en:

```
https://franquicia.onrender.com
```

La aplicación está desplegada en el plan gratuito de Render, el cual suspende automáticamente el servicio después de un período de inactividad. Al ejecutar el primer request desde Postman puede que tarde entre 30 segundos y 1 minuto en responder mientras el servicio se reactiva. Esto es completamente normal, los siguientes requests responderán con normalidad.

---

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- H2 (desarrollo local)
- MySQL (producción / Docker)
- Docker
- Terraform (infraestructura como código en AWS)

---

## Correr en local

### Requisitos
- Java 17
- Maven

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/Franquicia.git
cd Franquicia

# 2. Correr la aplicación
./mvnw spring-boot:run
```

La app estará disponible en `http://localhost:8080`

> La base de datos H2 en memoria se crea automáticamente al iniciar. Puedes ver la consola en `http://localhost:8080/h2-console` con la URL `jdbc:h2:mem:franquiciasdb` y usuario `sa` sin contraseña.

---
## Colección de Postman

En el repositorio se encuentra adjunta la colección de Postman lista para importar (`Franquicia.postman_collection.json`), la cual contiene todos los endpoints configurados y listos para probar.

## Correr con Docker

### Requisitos
- Docker Desktop instalado

### Pasos

```bash
# 1. Construir la imagen
docker build -t franquicias-api .

# 2. Correr el contenedor
docker run -p 8080:8080 franquicias-api
```

La app estará disponible en `http://localhost:8080`

---

## Infraestructura como código (Terraform)

En la carpeta `terraform/` se encuentra la definición de infraestructura en AWS lista para desplegar:

- **RDS MySQL** — base de datos en la nube
- **Elastic Beanstalk** — servidor para correr la app
- **Security Groups** — configuración de red

### Migrar de H2 a MySQL

Si se quiere correr con MySQL localmente, en `src/main/resources/application.properties` comentar la sección H2 y descomentar la sección MySQL.

### Desplegar infraestructura en AWS

```bash
cd terraform

# Inicializar Terraform
terraform init

# Ver los cambios que se van a aplicar
terraform plan

# Aplicar la infraestructura
terraform apply
```

> Se necesita tener configuradas las credenciales de AWS (`aws configure`) antes de ejecutar estos comandos.

---

## Colección de Postman

### Orden recomendado para probar los endpoints

Dado que la base de datos H2 es en memoria, es importante seguir este orden en cada sesión:

### 1. Crear una franquicia (ejecutar primero)
```
POST /api/franquicias
```
```json
{ "nombre": "Franquicia Test" }
```
> Guarda el `id` que te devuelve, lo necesitarás en los siguientes pasos.

---

### 2. Agregar sucursales a la franquicia (usar el id del paso 1)
```
POST /api/franquicias/{franquiciaId}/sucursales
```
```json
{ "nombre": "Sucursal Norte" }
```
```json
{ "nombre": "Sucursal Sur" }
```
> Guarda los `id` de cada sucursal.

---

### 3. Agregar productos a cada sucursal (usar el id de la sucursal)
```
POST /api/sucursales/{sucursalId}/productos
```
```json
{ "nombre": "Producto A", "stock": 10 }
```
```json
{ "nombre": "Producto B", "stock": 80 }
```

---

### 4. Actualizar nombre de franquicia
```
PATCH /api/franquicias/{id}/nombre
```
```json
{ "nombre": "Franquicia Actualizada" }
```

---

### 5. Actualizar nombre de sucursal
```
PATCH /api/franquicias/sucursales/{id}/nombre
```
```json
{ "nombre": "Sucursal Actualizada" }
```

---

### 6. Actualizar nombre de producto
```
PATCH /api/productos/{id}/nombre
```
```json
{ "nombre": "Producto Actualizado" }
```

---

### 7. Actualizar stock de producto
```
PATCH /api/productos/{id}/stock
```
```json
{ "stock": 999 }
```

---

### 8. Eliminar producto
```
DELETE /api/productos/{id}
```

---

### 9. Producto con más stock por sucursal (ejecutar después de tener productos creados)
```
GET /api/franquicias/{franquiciaId}/productos/top-stock
```

Respuesta esperada:
```json
[
  {
    "sucursalNombre": "Sucursal Norte",
    "productoNombre": "Producto B",
    "stock": 80
  },
  {
    "sucursalNombre": "Sucursal Sur",
    "productoNombre": "Producto C",
    "stock": 50
  }
]
```

---

## Resumen de endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/franquicias` | Crear franquicia |
| GET | `/api/franquicias` | Listar franquicias |
| GET | `/api/franquicias/{id}` | Buscar franquicia por id |
| PATCH | `/api/franquicias/{id}/nombre` | Actualizar nombre franquicia |
| POST | `/api/franquicias/{id}/sucursales` | Agregar sucursal |
| PATCH | `/api/franquicias/sucursales/{id}/nombre` | Actualizar nombre sucursal |
| POST | `/api/sucursales/{id}/productos` | Agregar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |
| PATCH | `/api/productos/{id}/stock` | Modificar stock |
| PATCH | `/api/productos/{id}/nombre` | Actualizar nombre producto |
| GET | `/api/franquicias/{id}/productos/top-stock` | Producto con más stock por sucursal |
