# ── Stage 1: Build ────────────────────────────────────────────────────────────
# Usa la imagen oficial de Maven que ya incluye JDK 21 y mvn.
# eclipse-temurin (la imagen anterior) no tiene Maven instalado, por eso
# los comandos "RUN mvn ..." fallaban en el build original.
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia solo el pom.xml primero y descarga las dependencias como capa separada.
# Mientras el pom.xml no cambie, Docker reutiliza esta capa en el caché
# aunque el código fuente sí haya cambiado → builds mucho más rápidos.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copia el código fuente y empaqueta el JAR
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
# Imagen mínima con solo el JRE (sin Maven ni fuentes), reduce el tamaño final.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Azure Container Apps enruta tráfico al puerto declarado en ingress (8080).
ENV PORT=8800
EXPOSE 8800

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8800/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
