# ============================================================
# DOCKERFILE MULTI-STAGE — Producción Optimizada (GCP/Docker)
# Superficie de ataque mínima + usuario No-Root + imagen ligera
# ============================================================

# ── ETAPA 1: Build (JDK completo para compilar) ──────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar solo el POM primero → aprovecha la caché de capas de Docker
# Las dependencias se descargan solo si el POM cambió, es decir se actualizan
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -q 2>/dev/null || true

# Copiar el código fuente y compilar
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests


# ── ETAPA 2: Extract layers (optimiza el tamaño de la imagen final) ──
FROM eclipse-temurin:21-jdk-alpine AS extractor

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

# Spring Boot Layered JAR: separa dependencias/snapshot/resources/classes
# para cachear capas que cambian con menos frecuencia
RUN java -Djarmode=layertools -jar app.jar extract


# ── ETAPA 3: Runtime final (JRE mínimo, NO JDK) ──────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Variables de entorno de producción
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"
ENV SERVER_PORT=8080

WORKDIR /app

# Copiar capas por orden de menor a mayor frecuencia de cambio
# → Maximiza reutilización de caché en builds sucesivos
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

# ── SEGURIDAD: Crear usuario y grupo no-root dedicado ──

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE ${SERVER_PORT}

# Health check nativo de Docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Usar JarLauncher de Spring Boot
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseContainerSupport", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "org.springframework.boot.loader.launch.JarLauncher"]
