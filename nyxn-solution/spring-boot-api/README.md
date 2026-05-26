# NYXN E-Commerce API — Spring Boot ////// Prueba tecnica Daniel Almanza

API RESTful para gestión de catálogo de productos, construida con **Java 21**, **Spring Boot 3.3**, **Arquitectura Hexagonal** y **Clean Architecture**.

## Stack Tecnologico usado

- Java 21 + Spring Boot 3.3
- PostgreSQL 16 (persistencia)
- Redis 7 (caché distribuido)
- Spring Data JPA + Hibernate
- Bean Validation (jakarta.validation)
- Springdoc OpenAPI / Swagger UI
- Docker + Docker Compose
- JUnit 5 + Mockito

## Estructura del Proyecto

```
src/main/java/com/nyxn/ecommerce/
├── domain/                    ← Núcleo: sin dependencias de frameworks
│   ├── model/Product.java
│   └── exception/
├── application/               ← Casos de uso + Puertos
│   ├── port/in/ProductUseCase.java        (Puerto de entrada)
│   ├── port/out/ProductRepository.java    (Puerto de salida)
│   └── usecase/ProductUseCaseImpl.java
└── infrastructure/            ← Adaptadores concretos
    ├── web/                   (REST Controller, DTOs, ExceptionHandler)
    ├── persistence/           (JPA Entity, Repository, Adapter)
    └── config/                (Redis, OpenAPI)
```

## Pasos que usamos para levantar el Proyecto

### Con Docker Compose (recomendado para leventar y ver en Docker o el navegador con swagger)
```bash
docker-compose up --build
```

### Local (requiere PostgreSQL y Redis corriendo)
```bash
./mvnw spring-boot:run
```

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/products` | Listar con paginación |
| GET | `/products/{id}` | Obtener por ID |
| POST | `/products` | Crear producto |
| PUT | `/products/{id}` | Actualizar producto |
| DELETE | `/products/{id}` | Eliminar producto |

**Swagger UI:** http://localhost:8080/swagger-ui.html

## Ejecutar Tests

```bash
./mvnw test
```

## Variables de Entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/nyxn_ecommerce` | URL PostgreSQL |
| `DB_USERNAME` | `postgres` | Usuario BD |
| `DB_PASSWORD` | `postgres` | Contraseña BD |
| `REDIS_HOST` | `localhost` | Host Redis |
| `REDIS_PORT` | `6379` | Puerto Redis |
