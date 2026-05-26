# NYXN — Prueba Técnica Full Stack Senior

Solución completa a la prueba técnica de NYXN para el cargo de Full Stack Developer.

## Estructura del Repositorio

```
nyxn-solution/
├── spring-boot-api/          ← Secciones 1, 2, 3, 4, 5 (Java + Spring Boot)
│   ├── src/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── pom.xml
│
└── node-notification-service/ ← Sección 6 (Node.js + TypeScript)
    ├── src/
    ├── package.json
    └── tsconfig.json
```

## Inicio Rápido

### API Spring Boot /// ejecutar en la terminal para construir
```bash
cd spring-boot-api
docker-compose up --build
# API disponible en: http://localhost:8080
# Swagger UI en:    http://localhost:8080/swagger-ui.html
```

### Notification Service
```bash
cd node-notification-service
npm install && npm run dev
# Servicio disponible en: http://localhost:3000
```

## Secciones Cubiertas

| Sección | Tema | Puntos |
|---------|------|--------|
| 1 | Java & Spring Boot — API RESTful + SOLID | 30 |
| 2 | Clean Architecture & Arquitectura Hexagonal | 25 |
| 3 | Control de Concurrencia (Optimistic/Pessimistic Lock) | 10 |
| 4 | PostgreSQL, OracleSQL, Redis Cache | 15 |
| 5 | GCP Cloud Run + Docker Multi-Stage + CI/CD | 10 |
| 6 | Node.js (Strategy Pattern) + Agente IA (Claude) | 10 |

Ver `NYXN_Prueba_Tecnica_Daniel_Almanza.pdf` para respuestas más detalladas.
