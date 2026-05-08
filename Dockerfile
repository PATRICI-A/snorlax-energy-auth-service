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

# AWS Lambda Web Adapter: intercepta el evento Lambda y lo convierte en una
# petición HTTP al servidor Spring Boot que corre localmente en PORT.
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:0.8.4 /lambda-adapter /opt/extensions/lambda-adapter

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# PORT debe coincidir con server.port de application.properties
ENV PORT=9090
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
