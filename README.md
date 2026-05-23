<div align="center">

# Auth Service — (M01 — Autenticación e Identidad)

### *"Gestión segura de identidad, sesiones y contraseñas para la red social universitaria PATRICI.A"*

---

### Stack Tecnológico

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-CloudAMQP-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

### Infraestructura & Calidad

![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality-F3702A?style=for-the-badge&logo=sonarcloud&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-%E2%89%A580%25-brightgreen?style=for-the-badge)

### Arquitectura

![Hexagonal](https://img.shields.io/badge/Architecture-Hexagonal-blueviolet?style=for-the-badge)
![REST API](https://img.shields.io/badge/REST-API-009688?style=for-the-badge)
![JWT](https://img.shields.io/badge/Auth-JWT%20HMAC--SHA256-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

</div>

---

## Tabla de Contenidos

1. [Integrantes](#1-integrantes)
2. [Tecnologías Utilizadas](#2-tecnologías-utilizadas)
3. [Descripción del Microservicio](#3-descripción-del-microservicio)
4. [Cómo Funciona](#4-cómo-funciona)
5. [Diagrama de Datos](#5-diagrama-de-datos)
6. [Diagrama de Clases](#6-diagrama-de-clases)
7. [Diagrama de Componentes](#7-diagrama-de-componentes)
8. [Funcionalidades Principales](#8-funcionalidades-principales)
9. [Endpoints](#9-endpoints)
10. [Colas de Mensajería](#10-colas-de-mensajería)
11. [Evidencia de Pruebas](#11-evidencia-de-pruebas)
12. [Evidencia de Cobertura](#12-evidencia-de-cobertura)
13. [Cómo Ejecutar](#13-cómo-ejecutar)
14. [Evidencia CI/CD](#14-evidencia-cicd)
15. [Link Swagger](#15-link-swagger)
16. [Estructura del Código](#16-estructura-del-código)
17. [Código Documentado](#17-código-documentado)
18. [Conexiones Externas](#18-conexiones-externas)
19. [Pipeline de Desarrollo](#19-pipeline-de-desarrollo)
20. [Pipeline de Producción](#20-pipeline-de-producción)
21. [Dockerizado](#21-dockerizado)
22. [Versionamiento](#22-versionamiento)

---

## 1. Integrantes

- Sebastian Castillejo
- Juan Melo
- Samuel Gil
- Maria Jose

---

## 2. Tecnologías Utilizadas

| **Tecnología / Herramienta** | **Uso principal en el proyecto** |
|---|---|
| **Java 21 (OpenJDK)** | Lenguaje base del módulo. LTS hasta 2029. |
| **Spring Boot 4.0.6** | Framework principal. Agrupa Security, Redis, OpenFeign y Swagger en un solo ecosistema. |
| **Spring Web** | Exposición de 9 endpoints REST bajo `/api/v1/auth/**`. |
| **Spring Security** | Configuración stateless, BCrypt, CORS y rutas públicas. |
| **JJWT (io.jsonwebtoken) 0.12.6** | Generación y validación de tokens JWT HMAC-SHA256. |
| **Spring Data Redis** | Caché de OTPs (TTL 10 min), refresh tokens (TTL 7 días) y bloqueos de cuenta. |
| **Spring AMQP (RabbitMQ)** | Publicación de eventos de email vía CloudAMQP. |
| **Spring Cloud OpenFeign 2025.1.1** | Cliente HTTP declarativo para llamadas al User Service (M02). |
| **MapStruct 1.6.3** | Mapeo entre requests REST y DTOs de aplicación. |
| **Lombok 1.18.36** | Reducción de boilerplate (builders, constructores, logs). |
| **JUnit 5** | Framework de pruebas unitarias. |
| **Mockito** | Simulación de dependencias en pruebas. |
| **JaCoCo 0.8.12** | Análisis de cobertura (mínimo 80% instrucciones configurado como regla). |
| **SpringDoc OpenAPI 2.8.6** | Generación automática de Swagger UI. |
| **SonarCloud** | Análisis estático de calidad en pipeline `sonar.yml`. |
| **Apache Maven 3.9+** | Gestión de dependencias y build. |
| **Docker 24.x** | Contenedorización con multi-stage build. |
| **GitHub Actions** | Pipelines CI (`ci.yml`), CD (`cd.yml`) y QA (`sonar.yml`). |
| **Redis 7-alpine** | Almacenamiento en memoria de sesiones, OTPs y bloqueos. |
| **RabbitMQ 3-management-alpine** | Broker para eventos de correo electrónico. |

---

## 3. Descripción del Microservicio

El microservicio de **Autenticación e Identidad** (M01) es el guardián de sesiones de la plataforma PATRICI.A. Sus responsabilidades principales son:

- **Verificación OTP:** genera un código de 6 dígitos, lo almacena en Redis (TTL 10 min) y lo publica en RabbitMQ para entrega por correo institucional.
- **Login seguro:** autentica con email y contraseña BCrypt. Bloquea la cuenta tras 5 intentos fallidos. Emite JWT (15 min) + refresh token (7 días).
- **Rotación de tokens:** intercambia un refresh token válido por un nuevo par JWT/refresh token, invalidando el anterior.
- **Recuperación de contraseña:** genera y publica un código de un solo uso por correo institucional.
- **Cambio de contraseña autenticado:** verifica la contraseña actual y actualiza con la nueva.

Puerto: `8080`. Este módulo **no persiste usuarios propios** — delega esa responsabilidad al User Service (M02) y actúa exclusivamente como guardián de sesiones y verificaciones. Toda la persistencia es en **Redis** mediante entidades `@RedisHash`.

**Requisitos funcionales que implementa este módulo:**

| RF | Nombre |
|---|---|
| RF-01 | Registro e Identidad — verificación OTP del correo institucional |
| RF-03 | Autenticación Segura — login con JWT, refresh token y bloqueo de cuenta |
| RF-07 | Recuperación de contraseña con código de un solo uso |
| RF-09 | Cambio de contraseña para usuarios autenticados |

---

## 4. Cómo Funciona

### Arquitectura Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────────┐
│                    EXTERIOR                         │
│  ┌──────────────┐         ┌──────────────────────┐  │
│  │  Controllers │         │  Redis Adapters      │  │
│  │  (REST)      │         │  Feign Adapter       │  │
│  │  Port In ──► │         │  RabbitMQ Adapter    │  │
│  └──────┬───────┘         └────────────┬─────────┘  │
│         │          DOMINIO             │ ◄ Port Out  │
│         ▼   ┌────────────────────┐    │             │
│         └──►│  Application       │◄───┘             │
│             │  Use Cases         │                   │
│             └────────────────────┘                   │
└─────────────────────────────────────────────────────┘
```

**Flujo de dependencias:** `Entrypoints / Infrastructure → Application → Domain`

### Patrones de Diseño

| Patrón | Ubicación | Descripción |
|---|---|---|
| **Ports & Adapters** | Toda la arquitectura | 9 puertos de entrada (uno por use case) + 3 de salida. |
| **Use Case por operación** | `LoginUseCase`, `ValidateOtpUseCase`, etc. | Una clase = una responsabilidad de negocio (SRP). |
| **Value Object** | `Email`, `OtpCode`, `Password`, `JwtToken`, `OtpEmbedded` | Encapsula validaciones de negocio en el propio tipo. |
| **Adapter** | `UserServiceFeignAdapter`, `EmailSenderAdapter`, `RefreshTokenRepositoryAdapter` | Conecta puertos de dominio con tecnologías externas. |
| **Repository (caché)** | `OtpRedisRepository`, `RefreshTokenRedisRepository`, etc. | Abstrae el almacenamiento Redis del dominio. |
| **Global Exception Handler** | `GlobalExceptionHandler` (@RestControllerAdvice) | Centraliza el mapeo de 10+ excepciones de dominio a códigos HTTP. |

### Conexión con Otros Microservicios

| Microservicio | Protocolo | Dirección | Dato |
|---|---|---|---|
| M02 — User Service | HTTP REST via OpenFeign | M01 → M02 | M01 consulta y actualiza usuarios en M02 (`/api/v1/internal/users/*`). Si M02 no responde → 503. |
| Servicio de Notificaciones | RabbitMQ (publicación) | M01 → RabbitMQ | M01 publica eventos `auth.otp.verification` y `auth.password.reset`. El consumidor entrega el correo. |
| Redis | Spring Data Redis | M01 → Redis | OTPs (TTL 10 min), refresh tokens (TTL 7 días), bloqueos de cuenta. Sin Redis el servicio no arranca. |

---

## 5. Diagrama de Datos

Este módulo no persiste datos en una base de datos relacional ni documental. Toda la información de sesión se almacena temporalmente en **Redis** mediante entidades `@RedisHash` con TTL definido. No aplica diagrama entidad-relación.

---

## 6. Diagrama de Clases

<div align="center">
<img src="src/main/resources/DiagramaClases.png" alt="Diagrama de Clases" width="700"/>
</div>

**Resumen del diseño de dominio:**

- **`User`** — entidad de dominio del usuario: contiene estado de verificación, lockout y OTPs.
- **`RefreshToken`** — entidad de sesión: contiene el par JWT/refresh token y su estado de revocación.
- **`Email`** — value object: encapsula y valida el formato del email institucional.
- **`OtpCode`** — value object: valida que el OTP sea exactamente 6 dígitos numéricos.
- **`OtpEmbedded`** — value object: OTP con timestamp de generación y estado de uso.
- **`Password`** — value object: valida la complejidad mínima de contraseña.
- **`JwtToken`** — value object: encapsula el string del JWT y su extracción de claims.
- **9 Ports In** — uno por use case: `LoginPort`, `InitVerificationPort`, `ValidateOtpPort`, `ResendOtpPort`, `LogoutPort`, `RefreshTokenPort`, `ForgotPasswordPort`, `ResetPasswordPort`, `ChangePasswordPort`.
- **3 Ports Out** — `UserServicePort`, `EmailSenderPort`, `RefreshTokenRepositoryPort`.

---

## 7. Diagrama de Componentes

<div align="center">
<img src="src/main/resources/componetes generales.png" alt="Diagrama de Componentes" width="700"/>
</div>

| Componente | Tipo | Interfaz |
|---|---|---|
| `AuthController` | REST Controller | `POST /api/v1/auth/**` (9 endpoints) |
| `LoginUseCase` | Application Use Case | Puerto In: `LoginPort` |
| `InitVerificationUseCase` | Application Use Case | Puerto In: `InitVerificationPort` |
| `ValidateOtpUseCase` | Application Use Case | Puerto In: `ValidateOtpPort` |
| `ResendOtpUseCase` | Application Use Case | Puerto In: `ResendOtpPort` |
| `LogoutUseCase` | Application Use Case | Puerto In: `LogoutPort` |
| `RefreshTokenUseCase` | Application Use Case | Puerto In: `RefreshTokenPort` |
| `ForgotPasswordUseCase` | Application Use Case | Puerto In: `ForgotPasswordPort` |
| `ResetPasswordUseCase` | Application Use Case | Puerto In: `ResetPasswordPort` |
| `ChangePasswordUseCase` | Application Use Case | Puerto In: `ChangePasswordPort` |
| `UserServiceFeignAdapter` | Driven Adapter | Puerto Out: `UserServicePort` → Feign HTTP |
| `EmailSenderAdapter` | Driven Adapter | Puerto Out: `EmailSenderPort` → RabbitMQ |
| `RefreshTokenRepositoryAdapter` | Driven Adapter | Puerto Out: `RefreshTokenRepositoryPort` → Redis |
| `JwtService` | Infrastructure Service | `generateToken()`, `validateToken()`, `extractUserId()` |
| `GlobalExceptionHandler` | Exception Handler | Mapeo de 10+ excepciones de dominio a códigos HTTP |

---

## 8. Funcionalidades Principales

| ID | RF | Funcionalidad | Descripción |
|---|---|---|---|
| F01 | RF-01 | **Inicializar verificación OTP** | Genera OTP de 6 dígitos con SecureRandom, lo guarda en Redis (TTL 10 min) y publica evento en RabbitMQ. |
| F02 | RF-01 | **Verificar OTP y activar cuenta** | Valida el OTP (máx. 3 intentos). Si es correcto, activa la cuenta en User Service y retorna JWT + refresh token. |
| F03 | RF-01 | **Reenviar OTP** | Genera y publica un nuevo OTP cuando el anterior expiró o se agotaron los 3 intentos. |
| F04 | RF-03 | **Login** | Autentica con email y contraseña BCrypt. Bloquea la cuenta tras 5 intentos fallidos. Retorna JWT (15 min) + refresh token (7 días). |
| F05 | RF-03 | **Renovar access token** | Intercambia un refresh token válido por un nuevo par JWT/refresh token (rotación). El anterior se invalida. |
| F06 | RF-03 | **Logout** | Requiere Bearer JWT. Extrae `userId` del token y elimina el refresh token de Redis. |
| F07 | RF-07 | **Olvidé mi contraseña** | Busca el usuario en User Service por email y publica evento de código de recuperación (TTL 10 min). |
| F08 | RF-07 | **Restablecer contraseña** | Valida el código de recuperación de uso único, hashea la nueva contraseña con BCrypt y la actualiza en User Service. |
| F09 | RF-09 | **Cambiar contraseña (autenticado)** | Requiere Bearer JWT. Verifica la contraseña actual y actualiza con la nueva contraseña hasheada. |

---

## 9. Endpoints

### Resumen

| Método | Endpoint | Funcionalidad | Auth requerida | Código exitoso |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/init-verification` | F01 — Inicializar OTP | No | 201 |
| `POST` | `/api/v1/auth/verify-otp` | F02 — Verificar OTP | No | 200 |
| `POST` | `/api/v1/auth/resend-otp` | F03 — Reenviar OTP | No | 200 |
| `POST` | `/api/v1/auth/login` | F04 — Login | No | 200 |
| `POST` | `/api/v1/auth/refresh` | F05 — Renovar access token | No | 200 |
| `POST` | `/api/v1/auth/logout` | F06 — Logout | Bearer JWT | 200 |
| `POST` | `/api/v1/auth/forgot-password` | F07 — Olvidé mi contraseña | No | 200 |
| `POST` | `/api/v1/auth/reset-password` | F08 — Restablecer contraseña | No | 200 |
| `POST` | `/api/v1/auth/change-password` | F09 — Cambiar contraseña | Bearer JWT | 200 |

---

### POST /api/v1/auth/init-verification — Inicializar Verificación OTP

<div align="center">
<img src="src/main/resources/INITVerificacion.png" alt="Init Verification" width="700"/>
</div>

**Request:**
```
POST /api/v1/auth/init-verification
```

```json
{
  "email": "usuario@escuelaing.edu.co",
  "hashedPassword": "$2a$10$eXaMpLeHaSh..."
}
```

```json
{ "message": "OTP sent to email" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 400 | Campos faltantes o inválidos | `"VALIDATION_ERROR"` |

---

### POST /api/v1/auth/verify-otp — Verificar OTP

<div align="center">
<img src="src/main/resources/VerifyOTP.png" alt="Verify OTP" width="700"/>
</div>

**Request:**
```json
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "123456"
}
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 422 | OTP inválido, ya usado o expirado | `"OTP_INVALID"` / `"OTP_EXPIRED"` |
| 429 | 3 intentos fallidos agotados | `"OTP_MAX_ATTEMPTS"` |

---

### POST /api/v1/auth/resend-otp — Reenviar OTP

<div align="center">
<img src="src/main/resources/ResendOTP.png" alt="Resend OTP" width="700"/>
</div>

**Request:**
```json
{ "email": "usuario@escuelaing.edu.co" }
```

```json
{ "message": "New OTP sent to email" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 422 | No existe cuenta con ese email | `"OTP_INVALID"` |

---

### POST /api/v1/auth/login — Login

<div align="center">
<img src="src/main/resources/Longin.png" alt="Login" width="700"/>
</div>

**Request:**
```json
{
  "email": "usuario@escuelaing.edu.co",
  "password": "MiContraseña123!"
}
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 401 | Contraseña incorrecta o usuario no encontrado | `"INVALID_CREDENTIALS"` |
| 403 | Email no verificado (OTP pendiente) | `"EMAIL_NOT_VERIFIED"` |
| 422 | Cuenta bloqueada por 5 intentos fallidos | `"ACCOUNT_LOCKED"` |

---

### POST /api/v1/auth/refresh — Renovar Access Token

<div align="center">
<img src="src/main/resources/RefreshToken.png" alt="Refresh Token" width="700"/>
</div>

**Request:**
```json
{ "refreshToken": "550e8400-e29b-41d4-a716-446655440000" }
```

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...(nuevo)...",
  "refreshToken": "661f9511-f3ac-52e5-b827-557766551111",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 401 | Refresh token inválido o expirado | `"TOKEN_INVALID"` / `"TOKEN_EXPIRED"` |

---

### POST /api/v1/auth/logout — Logout

<div align="center">
<img src="src/main/resources/Logout.png" alt="Logout" width="700"/>
</div>

**Request:**
```
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{ "message": "Session closed successfully" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 401 | JWT ausente o inválido | `"TOKEN_INVALID"` |

---

### POST /api/v1/auth/forgot-password — Olvidé Mi Contraseña

<div align="center">
<img src="src/main/resources/ForgotPassword.png" alt="Forgot Password" width="700"/>
</div>

**Request:**
```json
{ "email": "usuario@escuelaing.edu.co" }
```

```json
{ "message": "Recovery code sent to email" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 422 | No existe cuenta con ese email | `"OTP_INVALID"` |

---

### POST /api/v1/auth/reset-password — Restablecer Contraseña

<div align="center">
<img src="src/main/resources/ResetPassword.png" alt="Reset Password" width="700"/>
</div>

**Request:**
```json
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "654321",
  "newPassword": "NuevaContraseña456!"
}
```

```json
{ "message": "Password updated successfully" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 422 | Código inválido, ya usado o expirado | `"OTP_INVALID"` / `"OTP_EXPIRED"` |

---

### POST /api/v1/auth/change-password — Cambiar Contraseña

<div align="center">
<img src="src/main/resources/ChangePassword.png" alt="Change Password" width="700"/>
</div>

**Request:**
```
POST /api/v1/auth/change-password
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
{
  "currentPassword": "ContraActual123!",
  "newPassword": "ContraNueva456!"
}
```

```json
{ "message": "Password changed successfully" }
```

**Errores:**

| HTTP | Escenario | Mensaje |
|:---:|---|---|
| 401 | Contraseña actual incorrecta o JWT inválido | `"INVALID_CREDENTIALS"` / `"TOKEN_INVALID"` |

---

## 10. Colas de Mensajería

**Broker utilizado:** RabbitMQ (CloudAMQP en dev/prod, contenedor local en Docker Compose)

### Exchange

| Exchange | Tipo | Durable | Auto-delete |
|---|---|---|---|
| `auth.exchange` | `TopicExchange` | Sí | No |

### Tópicos / Colas que PUBLICA (produce)

| Routing Key | Clase DTO | Payload | Cuándo se publica |
|---|---|---|---|
| `auth.otp.verification` | `OtpVerificationEventDto` | `{ "to": "email", "otpCode": "123456" }` | `/init-verification` y `/resend-otp` |
| `auth.password.reset` | `PasswordResetEventDto` | `{ "to": "email", "code": "654321", "userId": "uuid" }` | `/forgot-password` |

### Tópicos / Colas que CONSUME (suscribe)

Este módulo **no consume** colas. Solo publica eventos. El servicio de notificaciones (consumidor) es responsable de la entrega del correo.

### Comportamiento ante fallo de RabbitMQ

Si RabbitMQ no está disponible, `EmailSenderAdapter` captura la excepción y loguea el OTP/código en consola (`INFO`). El flujo de negocio **no se interrumpe**.

---

## 11. Evidencia de Pruebas

### Clases de prueba implementadas

```
src/test/java/edu/eci/patricia/DOSW_patricia/
├── application/usecase/
│   ├── ChangePasswordUseCaseTest.java     → Cambio de contraseña con validación de contraseña actual
│   ├── ForgotPasswordUseCaseTest.java     → Generación y publicación de código de recuperación
│   ├── InitVerificationUseCaseTest.java   → Generación de OTP y envío del evento
│   ├── LoginUseCaseTest.java              → Login, bloqueo de cuenta y casos de error
│   ├── LogoutUseCaseTest.java             → Invalidación de refresh token en Redis
│   ├── RefreshTokenUseCaseTest.java       → Rotación de tokens y validación de expiración
│   ├── ResendOtpUseCaseTest.java          → Reenvío de OTP
│   ├── ResetPasswordUseCaseTest.java      → Validación de código y actualización de contraseña
│   └── ValidateOtpUseCaseTest.java        → Validación OTP, límite de intentos, activación de cuenta
├── domain/model/
│   ├── RefreshTokenTest.java              → Lógica de expiración y revocación
│   └── UserTest.java                      → verify(), incrementFailedAttempts(), lockAccount()
├── domain/valueobjects/
│   ├── EmailTest.java                     → Validación de formato de email
│   ├── JwtTokenTest.java                  → Encapsulamiento del JWT
│   ├── OtpCodeTest.java                   → Validación de 6 dígitos numéricos
│   ├── OtpEmbeddedTest.java               → OTP con timestamp
│   └── PasswordTest.java                  → Validación de complejidad de contraseña
├── entrypoints/advice/
│   └── GlobalExceptionHandlerTest.java    → Mapeo de excepciones a códigos HTTP
├── entrypoints/rest/controller/
│   └── AuthControllerTest.java            → Pruebas de integración del controlador REST
└── infrastructure/external/
    └── JwtServiceTest.java                → Generación, validación y extracción de claims JWT
```

### Cómo ejecutar las pruebas

```bash
# Pruebas unitarias
./mvnw test

# Todas las pruebas + reporte JaCoCo
./mvnw verify

# Reporte de cobertura
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html

# Prueba específica
./mvnw test -Dtest=LoginUseCaseTest
./mvnw test -Dtest=ValidateOtpUseCaseTest
./mvnw test -Dtest=RefreshTokenUseCaseTest
```

---

## 12. Evidencia de Cobertura

Cobertura mínima esperada > 80%.

<div align="center">
<img src="src/main/resources/jacoco.png" alt="Reporte de cobertura JaCoCo" width="700"/>
</div>

---

## 13. Cómo Ejecutar

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker & Docker Compose (solo para modo Docker)

### Opción 1: Local con Maven (perfil `dev`, sin Docker)

```bash
# Clonar repositorio
git clone https://github.com/PATRICI-A/snorlax-energy-auth-service.git
cd snorlax-energy-auth-service

# Levantar Redis localmente (requerido)
docker run -d -p 6379:6379 redis:7-alpine

# Ejecutar
./mvnw spring-boot:run
```

**URL:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Opción 2: Docker Compose (Redis + RabbitMQ)

```bash
docker compose up --build
```

### Variables de Entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil activo de Spring Boot |
| `JWT_SECRET` | `dev-secret-key-must-be-at-least-32-characters` | Clave HMAC-SHA256 (mín. 32 chars) |
| `JWT_EXPIRATION` | `900000` | Duración del access token en ms (15 min) |
| `REDIS_HOST` | `localhost` | Host de Redis |
| `REDIS_PORT` | `6379` | Puerto de Redis |
| `REDIS_PASSWORD` | _(vacío)_ | Contraseña de Redis |
| `REDIS_SSL` | `false` | Activar TLS para Redis |
| `RABBITMQ_HOST` | `woodpecker.rmq.cloudamqp.com` | Host de CloudAMQP |
| `RABBITMQ_PORT` | `5671` | Puerto AMQP con SSL |
| `RABBITMQ_USERNAME` | `thjdybjd` | Usuario CloudAMQP |
| `RABBITMQ_PASSWORD` | _(secreto)_ | Contraseña CloudAMQP |
| `RABBITMQ_VIRTUAL_HOST` | `thjdybjd` | Virtual host CloudAMQP |
| `RABBITMQ_SSL` | `true` | TLS para RabbitMQ |
| `USER_SERVICE_URL` | `http://localhost:8081` | URL base del User Service (M02) |
| `PORT` | `8080` | Puerto del servidor |

---

## 14. Evidencia CI/CD

El pipeline `.github/workflows/ci.yml` corre en cada push a `main` o `develop`:

<div align="center">
<img src="src/main/resources/evidencia despligue.png" alt="Pipeline CI GitHub Actions" width="700"/>
</div>

El pipeline `.github/workflows/cd.yml` se activa automáticamente cuando CI completa exitosamente en `main`:

<div align="center">
<img src="src/main/resources/despliegue.png" alt="Pipeline CD — Despliegue en Azure" width="700"/>
</div>

1. **Checkout** — `actions/checkout@v4`
2. **Java 21** — `actions/setup-java@v4` (Temurin)
3. **Cache Maven** — dependencias cacheadas para acelerar el build
4. **Build & Test** — `mvn clean verify -B` con Redis service container
5. **Upload JaCoCo** — artefacto `jacoco-report` (retención 14 días)
6. **Deploy JAR** — `azure/webapps-deploy@v3` hacia Azure App Service

---

## 15. Link Swagger

| Ambiente | URL |
|---|---|
| **Producción (Azure)** | `https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/swagger-ui/index.html` |
| **Local** | `http://localhost:8080/swagger-ui.html` |
| **Docker Compose** | `http://localhost:9090/swagger-ui.html` |
| **OpenAPI JSON** | `https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/v3/api-docs` |

> Usar **Bearer JWT** en el botón "Authorize" de Swagger UI para probar endpoints protegidos.

---

## 16. Estructura del Código

```
snorlax-energy-auth-service/
├── src/
│   ├── main/
│   │   ├── java/edu/eci/patricia/DOSW_patricia/
│   │   │   ├── domain/                              # CAPA DE DOMINIO
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── RefreshToken.java
│   │   │   │   ├── ports/
│   │   │   │   │   ├── in/
│   │   │   │   │   │   ├── LoginPort.java
│   │   │   │   │   │   ├── InitVerificationPort.java
│   │   │   │   │   │   ├── ValidateOtpPort.java
│   │   │   │   │   │   ├── ResendOtpPort.java
│   │   │   │   │   │   ├── LogoutPort.java
│   │   │   │   │   │   ├── RefreshTokenPort.java
│   │   │   │   │   │   ├── ForgotPasswordPort.java
│   │   │   │   │   │   ├── ResetPasswordPort.java
│   │   │   │   │   │   └── ChangePasswordPort.java
│   │   │   │   │   └── out/
│   │   │   │   │       ├── UserServicePort.java
│   │   │   │   │       ├── EmailSenderPort.java
│   │   │   │   │       └── RefreshTokenRepositoryPort.java
│   │   │   │   ├── exceptions/
│   │   │   │   │   ├── CuentaBloqueadaException.java
│   │   │   │   │   ├── EmailNotVerifiedException.java
│   │   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   │   ├── InvalidEmailDomainException.java
│   │   │   │   │   ├── OtpExpiredException.java
│   │   │   │   │   ├── OtpInvalidException.java
│   │   │   │   │   ├── OtpMaxAttemptsException.java
│   │   │   │   │   ├── TokenExpiredException.java
│   │   │   │   │   ├── TokenInvalidException.java
│   │   │   │   │   └── UserAlreadyExistsException.java
│   │   │   │   └── valueobjects/
│   │   │   │       ├── Email.java
│   │   │   │       ├── OtpCode.java
│   │   │   │       ├── OtpEmbedded.java
│   │   │   │       ├── Password.java
│   │   │   │       ├── JwtToken.java
│   │   │   │       ├── RolEnum.java
│   │   │   │       ├── Genero.java
│   │   │   │       ├── Interes.java
│   │   │   │       └── ProfileVisibility.java
│   │   │   │
│   │   │   ├── application/                         # CAPA DE APLICACIÓN
│   │   │   │   ├── usecase/
│   │   │   │   │   ├── LoginUseCase.java
│   │   │   │   │   ├── InitVerificationUseCase.java
│   │   │   │   │   ├── ValidateOtpUseCase.java
│   │   │   │   │   ├── ResendOtpUseCase.java
│   │   │   │   │   ├── LogoutUseCase.java
│   │   │   │   │   ├── RefreshTokenUseCase.java
│   │   │   │   │   ├── ForgotPasswordUseCase.java
│   │   │   │   │   ├── ResetPasswordUseCase.java
│   │   │   │   │   └── ChangePasswordUseCase.java
│   │   │   │   └── dto/
│   │   │   │       ├── request/
│   │   │   │       ├── response/
│   │   │   │       └── external/
│   │   │   │           └── UserDto.java
│   │   │   │
│   │   │   ├── infrastructure/                      # CAPA DE INFRAESTRUCTURA
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── adapter/
│   │   │   │   │   │   ├── UserServiceFeignAdapter.java
│   │   │   │   │   │   └── RefreshTokenRepositoryAdapter.java
│   │   │   │   │   └── cache/
│   │   │   │   │       ├── entity/
│   │   │   │   │       │   ├── OtpCache.java              # TTL 600 s
│   │   │   │   │       │   ├── PasswordResetOtpCache.java # TTL 600 s
│   │   │   │   │       │   ├── LockoutCache.java
│   │   │   │   │       │   └── RefreshTokenCache.java     # TTL 7 días
│   │   │   │   │       └── repository/
│   │   │   │   │           ├── OtpRedisRepository.java
│   │   │   │   │           ├── PasswordResetOtpRedisRepository.java
│   │   │   │   │           ├── LockoutRedisRepository.java
│   │   │   │   │           └── RefreshTokenRedisRepository.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── RabbitMQConfig.java
│   │   │   │   └── external/
│   │   │   │       ├── JwtService.java
│   │   │   │       ├── EmailSenderAdapter.java
│   │   │   │       ├── UserServiceFeignClient.java
│   │   │   │       └── dto/
│   │   │   │           ├── OtpVerificationEventDto.java
│   │   │   │           └── PasswordResetEventDto.java
│   │   │   │
│   │   │   ├── entrypoints/                         # CAPA DE ENTRADA
│   │   │   │   ├── rest/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   └── AuthController.java
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   └── AuthRestMapper.java
│   │   │   │   │   └── request/
│   │   │   │   └── advice/
│   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │       └── ErrorResponse.java
│   │   │   │
│   │   │   └── DoswPatriciaApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── DiagramaClases.png
│   │       ├── componetes generales.png
│   │       ├── despliegue.png
│   │       ├── evidencia despligue.png
│   │       ├── jacoco.png
│   │       ├── INITVerificacion.png
│   │       ├── VerifyOTP.png
│   │       ├── ResendOTP.png
│   │       ├── Longin.png
│   │       ├── RefreshToken.png
│   │       ├── Logout.png
│   │       ├── ForgotPassword.png
│   │       ├── ResetPassword.png
│   │       └── ChangePassword.png
│   │
│   └── test/
│       └── java/edu/eci/patricia/DOSW_patricia/
│           ├── application/usecase/
│           ├── domain/model/
│           ├── domain/valueobjects/
│           ├── entrypoints/advice/
│           ├── entrypoints/rest/
│           └── infrastructure/external/
│
├── .github/workflows/
│   ├── ci.yml
│   ├── cd.yml
│   └── sonar.yml
├── Dockerfile
├── docker-compose.yml
├── DEPLOYMENT_AZURE.md
├── pom.xml
└── README.md
```

---

## 17. Código Documentado

Todos los endpoints del `AuthController` están documentados con anotaciones OpenAPI:

```java
/**
 * Valida el OTP recibido por correo. Máximo 3 intentos.
 * Si es correcto activa la cuenta en User Service y retorna JWT + refresh token.
 * MAX_ATTEMPTS = 3
 */
public AuthResponse validateOtp(ValidateOtpCommand command) { ... }

/**
 * Autentica al usuario con email y contraseña BCrypt.
 * Bloquea la cuenta tras MAX_FAILED_ATTEMPTS = 5 intentos fallidos (~30 min en Redis).
 * Retorna JWT (15 min) + refresh token (7 días).
 */
public AuthResponse login(LoginCommand command) { ... }
```

Acceso a la documentación interactiva:
- **Swagger UI:** `/swagger-ui.html`
- **OpenAPI JSON:** `/v3/api-docs`

---

## 18. Conexiones Externas

| Módulo | Tipo | Dirección | Detalle |
|---|---|---|---|
| **M02 — User Service** | HTTP REST via OpenFeign | M01 → M02 | `UserServiceFeignClient` consulta y actualiza usuarios en M02 (`/api/v1/internal/users/*`). `FeignException` 404 → 404, otros → 503. |
| **Notificaciones** | RabbitMQ (publicación) | M01 → RabbitMQ | `EmailSenderAdapter` publica `OtpVerificationEventDto` y `PasswordResetEventDto` en `auth.exchange`. Si RabbitMQ falla → loguea en consola, flujo no se interrumpe. |
| **Redis** | Spring Data Redis | M01 → Redis | OTPs (TTL 10 min), refresh tokens (TTL 7 días), bloqueos de cuenta. Sin Redis el servicio no arranca. |
| **SonarCloud** | Análisis estático | GitHub Actions | `SONAR_TOKEN` en pipeline `sonar.yml`. Solo afecta QA, no la ejecución del servicio. |
| **Azure App Service** | Cloud hosting | CD pipeline | `azure/webapps-deploy@v3` con `AZURE_CREDENTIALS` y `AZURE_APP_SERVICE_NAME`. |

**M01 no consume eventos de otros módulos.** Solo publica.

---

## 19. Pipeline de Desarrollo

Perfil: **`dev`** — Redis local, sin PostgreSQL ni Docker requerido.

```bash
# Levantar en modo desarrollo
./mvnw spring-boot:run

# Ejecutar pruebas
./mvnw test

# Reporte de cobertura
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html
```

**No requiere:** PostgreSQL, Docker, Kafka.

---

## 20. Pipeline de Producción

Perfil: **`docker`** — Redis + RabbitMQ en contenedores.

```bash
# Build y levantamiento completo
docker compose up --build

# Solo auth-service + Redis
docker compose up auth-service redis

# Ver logs
docker compose logs -f auth-service

# Estado de contenedores
docker compose ps
```

**Servicios en `docker-compose.yml`:**

| Servicio | Imagen | Puerto |
|---|---|---|
| `auth-service` | Build local | `9090:9090` |
| `redis` | `redis:7-alpine` | `6379:6379` |
| `rabbitmq` | `rabbitmq:3-management-alpine` | `5672:5672`, `15672:15672` |
| `sonarqube` | `sonarqube:community` | `9000:9000` |
| `sonar-db` | `postgres:16` | interno |

---

## 21. Dockerizado

### Dockerfile (multi-etapa)

```dockerfile
# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Etapa 2: Runtime (imagen mínima)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Comandos Docker

```bash
# Primera vez
docker compose up --build

# Sin rebuild
docker compose up

# Detener y eliminar contenedores
docker compose down

# Eliminar también los volúmenes
docker compose down -v
```

---

## 22. Versionamiento

### Estrategia de Ramas (Git Flow)

| Rama | Propósito | Reglas |
|---|---|---|
| `main` | Versión estable para producción | Solo merges desde `feature/*` o `develop`. PR obligatorio + CI verde. Activa pipeline CD. |
| `develop` | Integración continua | Recibe merges desde `feature/*`. Activa el pipeline CI. |
| `feature/*` | Desarrollo de funcionalidad | Base: `develop`. Se fusiona con PR. |

### Convenciones de Ramas

```
feature/[nombre-funcionalidad]
hotfix/[descripcion-del-fix]
release/[version]
```

### Convenciones de Commits

```
[tipo]: [descripción específica de la acción]
```

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de errores |
| `docs` | Cambios en documentación |
| `test` | Agregar o modificar pruebas |
| `refactor` | Refactorización sin cambio de funcionalidad |
| `chore` | Cambios de configuración, dependencias |

---

<div align="center">

### Equipo **Snorlax Energy**

![Module](https://img.shields.io/badge/Module-M01_Auth_Service-orange?style=for-the-badge)
![Course](https://img.shields.io/badge/Course-DOSW-orange?style=for-the-badge)
![Year](https://img.shields.io/badge/Year-2026--1-blue?style=for-the-badge)

**Escuela Colombiana de Ingeniería Julio Garavito**

</div>
