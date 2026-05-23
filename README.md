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
| **Java 21 (OpenJDK)** | Lenguaje base del módulo. LTS. |
| **Spring Boot 4.0.6** | Framework principal. Agrupa Web, Security, Redis, AMQP y OpenAPI en un solo ecosistema. |
| **Spring Web** | Exposición de 9 endpoints REST bajo `/api/v1/auth/**`. |
| **Spring Security** | Configuración stateless, BCrypt para hashing de contraseñas y CORS. |
| **JJWT 0.12.6** | Generación y validación de tokens JWT con firma HMAC-SHA256. |
| **Spring Data Redis** | Caché de OTPs (TTL 10 min), refresh tokens (TTL 7 días) y bloqueos de cuenta. |
| **Spring AMQP (RabbitMQ)** | Publicación de eventos de email vía CloudAMQP. |
| **Spring Cloud OpenFeign 2025.1.1** | Cliente HTTP declarativo para llamadas al User Service (M02). |
| **MapStruct 1.6.3** | Mapeo entre requests REST y DTOs de aplicación. |
| **Lombok 1.18.36** | Reducción de boilerplate (builders, constructores, logs). |
| **JUnit 5** | Framework de pruebas unitarias. |
| **Mockito** | Simulación de dependencias en pruebas sin infraestructura real. |
| **JaCoCo 0.8.12** | Cobertura de pruebas con regla mínima del 80% de instrucciones. |
| **SpringDoc OpenAPI 2.8.6** | Generación automática de Swagger UI. |
| **SonarCloud** | Análisis estático de calidad en pipeline `sonar.yml`. |
| **Apache Maven 3.9+** | Gestión de dependencias y automatización de builds. |
| **Docker 24.x** | Contenedorización con multi-stage build (JDK → JRE alpine). |
| **GitHub Actions** | Pipelines CI (`ci.yml`), CD (`cd.yml`) y QA (`sonar.yml`). |
| **Redis 7-alpine** | Almacenamiento en memoria de sesiones, OTPs y bloqueos. |
| **RabbitMQ (CloudAMQP)** | Broker para eventos de correo electrónico. |

---

## 3. Descripción del Microservicio

El módulo M01 es el responsable de toda la gestión de identidad y autenticación dentro de la red social universitaria **PATRICI.A**. Sus responsabilidades principales son:

- **Registro con verificación OTP:** genera y valida códigos de un solo uso enviados al correo institucional.
- **Autenticación segura:** login con JWT de corta duración (15 min) y refresh tokens de larga duración (7 días) almacenados en Redis.
- **Gestión de sesiones:** rotación de tokens, logout y bloqueo automático de cuenta tras 5 intentos fallidos.
- **Recuperación y cambio de contraseña:** flujos completos con códigos de uso único y hashing BCrypt.

**Identificador:** M01 | **Repositorio:** `snorlax-energy-auth-service` | **Puerto:** 8080 (local / Azure) · 9090 (Docker Compose)

Este módulo **no persiste usuarios propios** — delega esa responsabilidad al User Service (M02) y actúa exclusivamente como guardián de sesiones y verificaciones.

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
│                  EXTERIOR                           │
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

### Patrones de Diseño Utilizados

| Patrón | Ubicación | Descripción |
|---|---|---|
| **Ports & Adapters** | Toda la arquitectura | 9 puertos de entrada + 3 de salida. |
| **Use Case por operación** | `LoginUseCase`, `ValidateOtpUseCase`, etc. | Una clase = una responsabilidad de negocio (SRP). |
| **Value Object** | `Email`, `OtpCode`, `Password`, `JwtToken`, `OtpEmbedded` | Encapsula validaciones de negocio en el propio tipo. |
| **Adapter** | `UserServiceFeignAdapter`, `EmailSenderAdapter`, `RefreshTokenRepositoryAdapter` | Conecta puertos de dominio con tecnologías externas. |
| **Repository (caché)** | `RefreshTokenRepositoryAdapter`, `OtpRedisRepository`, etc. | Abstrae el almacenamiento Redis del dominio. |
| **Global Exception Handler** | `GlobalExceptionHandler` (@RestControllerAdvice) | Centraliza el mapeo de excepciones de dominio a códigos HTTP. |

### Conexión con Otros Módulos

| Módulo | Protocolo | Dirección | Dato |
|---|---|---|---|
| M02 — User Service | HTTP REST via OpenFeign | M01 ↔ M02 | `userId`, `email`, `passwordHash`, `verified` vía `/api/v1/internal/users/*`. |
| Servicio de Email / Notificaciones | RabbitMQ (publicación) | M01 → RabbitMQ | Eventos `OtpVerificationEventDto` y `PasswordResetEventDto` en `auth.exchange`. |
| Redis | Spring Data Redis | M01 ↔ Redis | OTPs (TTL 10 min), refresh tokens (TTL 7 días), bloqueos de cuenta. |

---

## 5. Diagrama de Datos

### Entidades almacenadas en Redis (@RedisHash)

| Entidad | TTL | Campos clave |
|---|---|---|
| `OtpCache` | 600 s | `email`, `otpCode`, `attempts` |
| `PasswordResetOtpCache` | 600 s | `email`, `code`, `used` |
| `LockoutCache` | ~30 min | `email`, `lockedAt` |
| `RefreshTokenCache` | 7 días | `refreshToken` (ID), `userId`, `email`, `jwt`, `revoked`, `createdAt`, `expiraRefresh` |

### Campos del Refresh Token

| Campo | Tipo | Descripción |
|---|---|---|
| `refreshToken` (ID) | `String` | UUID del refresh token — clave del hash |
| `userId` | `String` | UUID del usuario propietario |
| `email` | `String` | Email del usuario |
| `jwt` | `String` | Access token JWT asociado |
| `revoked` | `boolean` | Si el token fue revocado (logout o rotación) |
| `createdAt` | `LocalDateTime` | Fecha de creación |
| `expiraRefresh` | `LocalDateTime` | Fecha de expiración del refresh token |

---

## 6. Diagrama de Clases

<div align="center">
<img src="src/main/resources/DiagramaClases.png" alt="Diagrama de Clases" width="600"/>
</div>

**Resumen del diseño de dominio:**

- **`User`** — entidad central: `email`, `passwordHash`, `verified`, `failedAttempts`, `locked`. Lógica: `verify()`, `incrementFailedAttempts()`, `lockAccount()`.
- **`RefreshToken`** — entidad de sesión: par JWT/refresh token y su estado de revocación.
- **`Email`** — value object que valida el formato del correo institucional.
- **`OtpCode`** — value object que valida 6 dígitos numéricos exactos.
- **`OtpEmbedded`** — OTP con timestamp de generación y estado de uso.
- **`Password`** — value object que valida la complejidad mínima de contraseña.
- **`JwtToken`** — encapsula el string del JWT y la extracción de claims.

### Clases principales del dominio

| Clase | Tipo | Responsabilidad |
|---|---|---|
| `User` | Model | Entidad de dominio — estado de verificación, lockout y OTPs |
| `RefreshToken` | Model | Entidad de sesión — par JWT/refresh token y revocación |
| `Email` | Value Object | Encapsula y valida el formato del email |
| `OtpCode` | Value Object | Valida que el OTP sea exactamente 6 dígitos numéricos |
| `OtpEmbedded` | Value Object | OTP con timestamp de generación y estado de uso |
| `Password` | Value Object | Valida la complejidad mínima de contraseña |
| `JwtToken` | Value Object | Encapsula el string del JWT y extracción de claims |
| `LoginPort` | Port In | Contrato del caso de uso de login |
| `InitVerificationPort` | Port In | Contrato del caso de uso de inicio de verificación OTP |
| `ValidateOtpPort` | Port In | Contrato del caso de uso de validación OTP |
| `ResendOtpPort` | Port In | Contrato del caso de uso de reenvío de OTP |
| `LogoutPort` | Port In | Contrato del caso de uso de logout |
| `RefreshTokenPort` | Port In | Contrato del caso de uso de renovación de token |
| `ForgotPasswordPort` | Port In | Contrato del caso de uso de olvidé mi contraseña |
| `ResetPasswordPort` | Port In | Contrato del caso de uso de restablecimiento de contraseña |
| `ChangePasswordPort` | Port In | Contrato del caso de uso de cambio de contraseña autenticado |
| `UserServicePort` | Port Out | Contrato para consultar/actualizar usuarios en el User Service |
| `EmailSenderPort` | Port Out | Contrato para publicar eventos de email |
| `RefreshTokenRepositoryPort` | Port Out | Contrato para persistir/consultar refresh tokens en Redis |

---

## 7. Diagrama de Componentes


### Específico

<div align="center">
<img src="src/main/resources/componentes especificos.png" alt="Diagrama de Componentes Específico" width="600"/>
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

<div align="center">

| ID | RF | Funcionalidad | Descripción |
|---|---|---|---|
| F01 | RF-01 | **Inicializar verificación OTP** | Genera OTP de 6 dígitos con SecureRandom, lo guarda en Redis (TTL 10 min) y publica evento en RabbitMQ. Llamado por el Registration Service tras crear el usuario. |
| F02 | RF-01 | **Verificar OTP y activar cuenta** | Valida el OTP (máx. 3 intentos). Si es correcto, activa la cuenta en User Service y retorna JWT + refresh token. |
| F03 | RF-01 | **Reenviar OTP** | Genera y publica un nuevo OTP. Usar cuando el anterior expiró (10 min) o se agotaron los 3 intentos. |
| F04 | RF-03 | **Login** | Autentica con email y contraseña BCrypt. Bloquea la cuenta tras 5 intentos fallidos. Retorna JWT (15 min) + refresh token (7 días). |
| F05 | RF-03 | **Renovar access token** | Intercambia un refresh token válido por un nuevo par JWT/refresh token (rotación). El refresh token anterior se invalida. |
| F06 | RF-03 | **Logout** | Requiere Bearer JWT. Extrae `userId` del token y elimina el refresh token de Redis, cerrando la sesión. |
| F07 | RF-07 | **Olvidé mi contraseña** | Busca el usuario en User Service por email y publica evento de código de recuperación de 6 dígitos en RabbitMQ (TTL 10 min). |
| F08 | RF-07 | **Restablecer contraseña** | Valida el código de recuperación de uso único, hashea la nueva contraseña con BCrypt y la actualiza en User Service via Feign. |
| F09 | RF-09 | **Cambiar contraseña (autenticado)** | Requiere Bearer JWT. Verifica la contraseña actual contra el hash en User Service, luego actualiza con la nueva contraseña hasheada. |

</div>

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
<img src="src/main/resources/INITVerificacion.png" alt="Init Verification" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/init-verification
```

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional del usuario recién registrado |
| `hashedPassword` | `String` | body | Sí | Contraseña ya hasheada con BCrypt (viene del Registration Service) |

```json
// Request body
{
  "email": "usuario@escuelaing.edu.co",
  "hashedPassword": "$2a$10$eXaMpLeHaSh..."
}
```

```json
// Response
{ "message": "OTP sent to email" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 400 | Campos faltantes o inválidos | `VALIDATION_ERROR` |

---

### POST /api/v1/auth/verify-otp — Verificar OTP

<div align="center">
<img src="src/main/resources/VerifyOTP.png" alt="Verify OTP" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/verify-otp
```

```json
// Request body
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "123456"
}
```

```json
// Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 422 | OTP inválido, ya usado o expirado | `OTP_INVALID` / `OTP_EXPIRED` |
| 429 | 3 intentos fallidos agotados | `OTP_MAX_ATTEMPTS` |

---

### POST /api/v1/auth/resend-otp — Reenviar OTP

<div align="center">
<img src="src/main/resources/ResendOTP.png" alt="Resend OTP" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/resend-otp
```

```json
// Request body
{ "email": "usuario@escuelaing.edu.co" }
```

```json
// Response
{ "message": "New OTP sent to email" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 422 | No existe cuenta con ese email | `OTP_INVALID` |

---

### POST /api/v1/auth/login — Login

<div align="center">
<img src="src/main/resources/Longin.png" alt="Login" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/login
```

```json
// Request body
{
  "email": "usuario@escuelaing.edu.co",
  "password": "MiContraseña123!"
}
```

```json
// Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 401 | Contraseña incorrecta o usuario no encontrado | `INVALID_CREDENTIALS` |
| 403 | Email no verificado (OTP pendiente) | `EMAIL_NOT_VERIFIED` |
| 422 | Cuenta bloqueada por 5 intentos fallidos | `ACCOUNT_LOCKED` |

---

### POST /api/v1/auth/refresh — Renovar Access Token

<div align="center">
<img src="src/main/resources/RefreshToken.png" alt="Refresh Token" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/refresh
```

```json
// Request body
{ "refreshToken": "550e8400-e29b-41d4-a716-446655440000" }
```

```json
// Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...(nuevo)...",
  "refreshToken": "661f9511-f3ac-52e5-b827-557766551111",
  "tokenType": "Bearer"
}
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 401 | Refresh token inválido o expirado | `TOKEN_INVALID` / `TOKEN_EXPIRED` |

---

### POST /api/v1/auth/logout — Logout

<div align="center">
<img src="src/main/resources/Logout.png" alt="Logout" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
// Response
{ "message": "Session closed successfully" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 401 | JWT ausente o inválido | `TOKEN_INVALID` |

---

### POST /api/v1/auth/forgot-password — Olvidé Mi Contraseña

<div align="center">
<img src="src/main/resources/ForgotPassword.png" alt="Forgot Password" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/forgot-password
```

```json
// Request body
{ "email": "usuario@escuelaing.edu.co" }
```

```json
// Response
{ "message": "Recovery code sent to email" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 422 | No existe cuenta con ese email | `OTP_INVALID` |

---

### POST /api/v1/auth/reset-password — Restablecer Contraseña

<div align="center">
<img src="src/main/resources/ResetPassword.png" alt="Reset Password" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/reset-password
```

```json
// Request body
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "654321",
  "newPassword": "NuevaContraseña456!"
}
```

```json
// Response
{ "message": "Password updated successfully" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 422 | Código inválido, ya usado o expirado | `OTP_INVALID` / `OTP_EXPIRED` |

---

### POST /api/v1/auth/change-password — Cambiar Contraseña (Autenticado)

<div align="center">
<img src="src/main/resources/ChangePassword.png" alt="Change Password" width="600"/>
</div>

**Request:**
```
POST /api/v1/auth/change-password
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
// Request body
{
  "currentPassword": "ContraActual123!",
  "newPassword": "ContraNueva456!"
}
```

```json
// Response
{ "message": "Password changed successfully" }
```

**Errores:**

| HTTP | Escenario | Código de error |
|:---:|---|---|
| 401 | Contraseña actual incorrecta o JWT inválido | `INVALID_CREDENTIALS` / `TOKEN_INVALID` |

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
│   ├── ChangePasswordUseCaseTest.java
│   ├── ForgotPasswordUseCaseTest.java
│   ├── InitVerificationUseCaseTest.java
│   ├── LoginUseCaseTest.java
│   ├── LogoutUseCaseTest.java
│   ├── RefreshTokenUseCaseTest.java
│   ├── ResendOtpUseCaseTest.java
│   ├── ResetPasswordUseCaseTest.java
│   └── ValidateOtpUseCaseTest.java
├── domain/model/
│   ├── RefreshTokenTest.java
│   └── UserTest.java
├── domain/valueobjects/
│   ├── EmailTest.java
│   ├── JwtTokenTest.java
│   ├── OtpCodeTest.java
│   ├── OtpEmbeddedTest.java
│   └── PasswordTest.java
├── entrypoints/advice/
│   └── GlobalExceptionHandlerTest.java
├── entrypoints/rest/controller/
│   └── AuthControllerTest.java
└── infrastructure/external/
    └── JwtServiceTest.java
```

### Cómo ejecutar las pruebas

```bash
# Pruebas unitarias (requiere Redis en localhost:6379)
./mvnw test

# Prueba específica
./mvnw test -Dtest=LoginUseCaseTest

# Todas las pruebas + reporte JaCoCo + verificar cobertura ≥ 80%
./mvnw verify

# Reporte de cobertura
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html
```

<div align="center">
<img src="src/main/resources/unitarias.png" alt="Evidencia pruebas unitarias" width="600"/>
</div>

---

## 12. Evidencia de Cobertura

Cobertura mínima configurada: **≥ 80% de instrucciones**.

<div align="center">
<img src="src/main/resources/pruebas_auth.png" alt="Reporte de cobertura JaCoCo" width="600"/>
</div>

| Métrica | Objetivo | Obtenido |
|---|---|---|
| Cobertura de instrucciones | ≥ 80% | 97% |
| Cobertura de ramas | ≥ 60% | 93% |
| Clases cubiertas | — | 29 de 29 |

---

## 13. Cómo Ejecutar

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Una instancia de Redis accesible
- RabbitMQ / CloudAMQP (opcional en local)
- URL del User Service (M02) configurada

### Opción 1: Local con Maven (perfil `dev`)

```bash
# Clonar el repositorio
git clone https://github.com/PATRICI-A/snorlax-energy-auth-service.git
cd snorlax-energy-auth-service

# Levantar Redis localmente (si no lo tienes)
docker run -d -p 6379:6379 redis:7-alpine

# Ejecutar
./mvnw spring-boot:run
```

**URL:** `http://localhost:8080`  
**Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Opción 2: Docker Compose (recomendado)

```bash
# Levanta auth-service + Redis + RabbitMQ + SonarQube
docker compose up --build

# Solo auth-service + Redis (más liviano)
docker compose up auth-service redis

# Ver logs en tiempo real
docker compose logs -f auth-service

# Detener y eliminar volúmenes
docker compose down -v
```

### Variables de Entorno

| Variable | Valor por defecto (dev) | Descripción |
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
| `SERVER_URL` | `http://localhost:8080` | URL base mostrada en Swagger |

---

## 14. Evidencia CI/CD

El pipeline `.github/workflows/ci.yml` corre en cada push a `main` o `develop` y en cada PR hacia esas ramas:

<div align="center">
<img src="src/main/resources/evidencia despligue.png" alt="Evidencia de despliegue GitHub Actions" width="600"/>
</div>

El pipeline `.github/workflows/cd.yml` se activa automáticamente cuando el CI completa exitosamente en `main`:

<div align="center">
<img src="src/main/resources/despliegue.png" alt="Despliegue en Azure App Service" width="600"/>
</div>

---

## 15. Link Swagger

| Ambiente | URL |
|---|---|
| **Producción (Azure)** | https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/swagger-ui/index.html |
| **Local** | http://localhost:8080/swagger-ui.html |
| **Docker Compose** | http://localhost:9090/swagger-ui.html |
| **OpenAPI JSON** | https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/v3/api-docs |

> Usar **Bearer JWT** en el botón "Authorize" de Swagger UI para probar endpoints protegidos (`/logout`, `/change-password`).

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
│   │   │   │   │       │   ├── OtpCache.java
│   │   │   │   │       │   ├── PasswordResetOtpCache.java
│   │   │   │   │       │   ├── LockoutCache.java
│   │   │   │   │       │   └── RefreshTokenCache.java
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
│   │       └── application-dev.yml
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
 * Autentica al usuario con email y contraseña.
 * Bloquea la cuenta tras MAX_FAILED_ATTEMPTS = 5 intentos fallidos.
 * Retorna JWT (15 min) + refresh token (7 días) en caso exitoso.
 */
@Operation(summary = "Login", description = "Autentica con email y contraseña BCrypt.")
@ApiResponses({
  @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
  @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
  @ApiResponse(responseCode = "422", description = "Cuenta bloqueada")
})
public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequest request) { ... }
```

Anotaciones usadas en todos los endpoints:
- `@Tag(name = "Authentication", description = "...")` — categoría del controlador en Swagger UI
- `@Operation(summary = "...", description = "...")` — propósito y comportamiento de cada endpoint
- `@ApiResponses({@ApiResponse(responseCode = "...", description = "...")})` — todos los códigos de respuesta posibles
- `@SecurityRequirement(name = "bearerAuth")` — marca los endpoints que requieren JWT Bearer

Acceso a la documentación interactiva:
- **Swagger UI:** `/swagger-ui.html` (también en la raíz `/` por `use-root-path: true`)
- **OpenAPI JSON:** `/v3/api-docs`

---

## 18. Conexiones Externas

| Servicio | Tipo | Dirección | Detalle | Manejo de fallo |
|---|---|---|---|---|
| **Redis** | Caché en memoria | M01 ↔ Redis | OTPs (TTL 10 min), refresh tokens (TTL 7 días), bloqueos. Config: `REDIS_HOST`, `REDIS_PORT`, `REDIS_SSL`. | Sin Redis el servicio no arranca. |
| **RabbitMQ (CloudAMQP)** | Broker AMQP | M01 → RabbitMQ | Publica eventos `auth.otp.verification` y `auth.password.reset`. Config: `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_SSL`. | `EmailSenderAdapter` loguea el código en consola — el flujo no se interrumpe. |
| **User Service (M02)** | HTTP REST via OpenFeign | M01 ↔ M02 | Buscar usuario por email/id, marcar como verificado, actualizar contraseña. Config: `USER_SERVICE_URL`. | `GlobalExceptionHandler` captura `FeignException`: `404` → 404, otros → 503. |
| **SonarCloud** | Análisis estático | CI pipeline | Cobertura y calidad de código. Config: `SONAR_TOKEN`. | Solo afecta el pipeline de QA, no la ejecución del servicio. |
| **Azure App Service** | Cloud hosting | CD pipeline | Despliegue del JAR en producción. Config: `AZURE_CREDENTIALS`, `AZURE_APP_SERVICE_NAME`. | El pipeline CD falla y notifica; no hay impacto en el servicio ya desplegado. |

---

## 19. Pipeline de Desarrollo

Perfil: **`dev`** — Redis local, RabbitMQ vía CloudAMQP, sin Docker obligatorio.

```bash
# Levantar en modo desarrollo
docker run -d -p 6379:6379 redis:7-alpine
./mvnw spring-boot:run

# Ejecutar pruebas
./mvnw test

# Reporte de cobertura
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html
```

El pipeline `.github/workflows/ci.yml` se ejecuta en cada push a `main` o `develop` y en cada PR:

```yaml
name: CI – Build & Test
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
    types: [opened, synchronize, reopened]

jobs:
  build-test:
    runs-on: ubuntu-latest
    services:
      redis:
        image: redis:7-alpine
        ports: ['6379:6379']
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
    steps:
      - uses: actions/checkout@v4
        with: { fetch-depth: 0 }
      - uses: actions/setup-java@v4
        with: { java-version: '21', distribution: temurin, cache: maven }
      - run: mvn clean verify -B
        env:
          JWT_SECRET: ${{ secrets.JWT_SECRET_TEST }}
          REDIS_HOST: localhost
          USER_SERVICE_URL: 'http://localhost:8099'
          SPRING_PROFILES_ACTIVE: dev
```

| Paso | Acción | Descripción |
|---|---|---|
| 1 | Checkout | `actions/checkout@v4` con `fetch-depth: 0` |
| 2 | Setup JDK 21 | `actions/setup-java@v4` distribución Temurin, caché Maven |
| 3 | Redis service | Contenedor `redis:7-alpine` con healthcheck |
| 4 | Build & Test | `mvn clean verify -B` — falla si cobertura < 80% |
| 5 | Upload JaCoCo | Artefacto `jacoco-report` (retención 14 días) |
| 6 | Upload Surefire | Artefacto `surefire-reports` (retención 14 días) |
| 7 | Upload JAR | Artefacto `auth-service-jar` (retención 14 días) |

**Secrets requeridos:** `JWT_SECRET_TEST`, `SONAR_TOKEN`, `GITHUB_TOKEN`

---

## 20. Pipeline de Producción

Perfil: **producción en Azure** — el pipeline CD se activa cuando el CI completa exitosamente en `main`.

```bash
# Build y levantamiento con Docker Compose
docker compose up --build

# Solo reconstruir el auth-service
docker compose up --build auth-service

# Ver logs
docker compose logs -f auth-service

# Detener y eliminar volúmenes
docker compose down -v
```

**Servicios en `docker-compose.yml`:**

| Servicio | Imagen | Puerto |
|---|---|---|
| `auth-service` | Build local | `9090:9090` |
| `redis` | `redis:7-alpine` | `6379:6379` |
| `rabbitmq` | `rabbitmq:3-management-alpine` | `5672:5672`, `15672:15672` |
| `sonarqube` | `sonarqube:community` | `9000:9000` |
| `sonar-db` | `postgres:16` | interno |

```yaml
name: CD – Deploy to Azure
on:
  workflow_run:
    workflows: ["CI – Build & Test"]
    branches: [main]
    types: [completed]

jobs:
  deploy-to-azure:
    runs-on: ubuntu-latest
    if: github.event.workflow_run.conclusion == 'success'
    steps:
      - uses: actions/checkout@v4
      - run: mvn package -DskipTests -B
      - uses: azure/login@v2
        with: { creds: ${{ secrets.AZURE_CREDENTIALS }} }
      - uses: azure/webapps-deploy@v3
        with:
          app-name: ${{ secrets.AZURE_APP_SERVICE_NAME }}
          package: target/auth-service-*.jar
```

| Paso | Acción | Descripción |
|---|---|---|
| 1 | Checkout | `actions/checkout@v4` |
| 2 | Setup JDK 21 | `actions/setup-java@v4` con caché Maven |
| 3 | Build JAR | `mvn package -DskipTests -B` |
| 4 | Azure Login | `azure/login@v2` usando `AZURE_CREDENTIALS` |
| 5 | Deploy JAR | `azure/webapps-deploy@v3` |

**Secrets requeridos:**

| Secret | Descripción |
|---|---|
| `AZURE_CREDENTIALS` | JSON del Service Principal |
| `AZURE_APP_SERVICE_NAME` | Nombre del App Service en Azure |
| `JWT_SECRET_TEST` | Clave JWT para CI (≥ 32 caracteres) |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |

---

## 21. Dockerizado

### Dockerfile (Multi-stage Build)

```dockerfile
# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia pom.xml primero → dependencias cacheadas como capa separada
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
# Imagen mínima con solo JRE (~90 MB vs ~500 MB del JDK)
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
# Primera vez (con todos los servicios)
docker compose up --build

# Solo auth-service + Redis
docker compose up auth-service redis

# Detener y eliminar contenedores
docker compose down

# Eliminar también los volúmenes
docker compose down -v
```

> **Nota:** `docker-compose.yml` expone el puerto `9090` localmente. Para despliegue en Azure (CD pipeline) se usa el `Dockerfile` directamente y Azure enruta al puerto `8080`.

---

## 22. Versionamiento

### Estrategia de Ramas (Git Flow)

| Rama | Propósito | Reglas |
|---|---|---|
| `main` | Versión estable lista para producción | Solo merges mediante PR. Cada merge exitoso activa el pipeline CD hacia Azure. |
| `develop` | Integración continua de trabajo | Recibe merges desde `feature/*`. Activa el pipeline CI. |
| `feature/*` | Desarrollo de funcionalidades | Base: `develop`. Se fusiona a `develop` mediante PR revisado. |

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
| `refactor` | Refactorización sin cambio de comportamiento |
| `test` | Adición o modificación de pruebas |
| `chore` | Cambios de configuración, build o dependencias |

---

<div align="center">

### Equipo Snorlax Energy

![Module](https://img.shields.io/badge/Module-M01_Auth_Service-orange?style=for-the-badge)
![Course](https://img.shields.io/badge/Course-DOSW-orange?style=for-the-badge)
![Year](https://img.shields.io/badge/Year-2026--1-blue?style=for-the-badge)

**Escuela Colombiana de Ingeniería Julio Garavito**

</div>
