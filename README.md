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
3. [Descripción del Módulo](#3-descripción-del-módulo)
4. [Cómo Funciona el Módulo](#4-cómo-funciona-el-módulo)
5. [Diagrama de Datos](#5-diagrama-de-datos)
6. [Diagrama de Clases](#6-diagrama-de-clases)
7. [Diagrama de Componentes](#7-diagrama-de-componentes)
8. [Funcionalidades del Módulo](#8-funcionalidades-del-módulo)
9. [Endpoints Expuestos](#9-endpoints-expuestos)
10. [Colas de Mensajería](#10-colas-de-mensajería)
11. [Evidencia de Pruebas Unitarias](#11-evidencia-de-pruebas-unitarias)
12. [Evidencia de Análisis de Cobertura](#12-evidencia-de-análisis-de-cobertura)
13. [Cómo Ejecutar el Proyecto](#13-cómo-ejecutar-el-proyecto)
14. [Evidencia del Despliegue CI/CD](#14-evidencia-del-despliegue-cicd)
15. [Link Swagger Desplegado](#15-link-swagger-desplegado)
16. [Estructura del Código](#16-estructura-del-código)
17. [Código Documentado](#17-código-documentado)
18. [Conexiones con Servicios Externos](#18-conexiones-con-servicios-externos)
19. [Pipeline de Desarrollo](#19-pipeline-de-desarrollo)
20. [Pipeline de Producción](#20-pipeline-de-producción)
21. [Dockerizado](#21-dockerizado)
22. [Estrategia de Versionamiento](#22-estrategia-de-versionamiento)

---

## 1. Integrantes

| Nombre | Rol |
|---|---|
| Sebastian Castillejo | Backend Developer |
| Juan Melo | Backend Developer |
| Samuel Gil | Backend Developer |
| Maria Jose | Backend Developer |

---

## 2. Tecnologías Utilizadas

| Tecnología / Herramienta | Versión | Uso en el módulo |
|---|---|---|
| Java (OpenJDK) | 21 LTS | Lenguaje base del módulo |
| Spring Boot | 4.0.6 | Framework principal |
| Spring Web | Incluido | Exposición de endpoints REST |
| Spring Security | Incluido | Configuración stateless, BCrypt, CORS |
| JJWT (io.jsonwebtoken) | 0.12.6 | Generación y validación de tokens JWT HMAC-SHA256 |
| Spring Data Redis | Incluido | Caché de OTPs (TTL 10 min), refresh tokens (TTL 7 días) y bloqueos de cuenta |
| Spring AMQP (RabbitMQ) | Incluido | Publicación de eventos de email vía CloudAMQP |
| Spring Cloud OpenFeign | 2025.1.1 (BOM) | Cliente HTTP declarativo para llamadas al User Service (M02) |
| MapStruct | 1.6.3 | Mapeo entre requests REST y DTOs de aplicación |
| Lombok | 1.18.36 | Reducción de boilerplate (builders, constructores, logs) |
| JUnit 5 | Incluido | Pruebas unitarias |
| Mockito | Incluido | Simulación de dependencias en pruebas |
| JaCoCo | 0.8.12 | Análisis de cobertura (mínimo 80% instrucciones configurado como regla) |
| SpringDoc OpenAPI | 2.8.6 | Generación automática de Swagger UI |
| SonarCloud | N/A | Análisis estático de calidad en pipeline `sonar.yml` |
| Apache Maven | 3.9+ | Gestión de dependencias y build |
| Docker | 24.x | Contenedorización con multi-stage build |
| GitHub Actions | N/A | Pipelines CI (`ci.yml`), CD (`cd.yml`) y QA (`sonar.yml`) |
| Redis | 7-alpine | Almacenamiento en memoria de sesiones, OTPs y bloqueos |
| RabbitMQ (CloudAMQP) | 3-management-alpine | Broker para eventos de correo electrónico |

---

## 3. Descripción del Módulo

**Identificador:** M01  
**Nombre técnico del repositorio:** `snorlax-energy-auth-service`  
**Puerto local:** 8080  
**Puerto Docker:** 8080 (CD Azure) / 9090 (docker-compose local)

El módulo M01 es el responsable de toda la gestión de identidad y autenticación dentro de la red social universitaria **PATRICI.A**. Implementa el registro con verificación OTP por correo institucional, inicio de sesión seguro con tokens JWT de corta duración y refresh tokens de larga duración almacenados en Redis, y un flujo completo de recuperación y cambio de contraseña.

Este módulo **no persiste usuarios propios** — delega esa responsabilidad al User Service (M02) y actúa exclusivamente como guardián de sesiones y verificaciones.

**Requisitos funcionales que implementa este módulo:**

| RF | Nombre |
|---|---|
| RF-01 | Registro e Identidad — verificación OTP del correo institucional |
| RF-03 | Autenticación Segura — login con JWT, refresh token y bloqueo de cuenta |
| RF-07 | Recuperación de contraseña con código de un solo uso |
| RF-09 | Cambio de contraseña para usuarios autenticados |

---

## 4. Cómo Funciona el Módulo

### Estilo de Arquitectura: Hexagonal (Ports & Adapters)

El módulo implementa Arquitectura Hexagonal. El dominio no conoce frameworks ni bases de datos. Los controladores REST y los adaptadores de Redis/Feign/RabbitMQ implementan puertos (interfaces). El flujo de dependencias es unidireccional:

```
Entrypoints (REST) → Application (Use Cases) → Domain (Ports)
                                                      ↑
Infrastructure (Redis / Feign / RabbitMQ adapters) ──┘
```

### Patrones de Diseño Utilizados

| Patrón | Clase(s) donde aplica | Por qué se usa |
|---|---|---|
| Ports & Adapters | Todos los puertos `in/` y `out/` | Desacopla el dominio de Spring, Redis, Feign y RabbitMQ |
| Use Case por operación | `LoginUseCase`, `ValidateOtpUseCase`, `ForgotPasswordUseCase`, etc. | Una clase = una responsabilidad de negocio (SRP) |
| Value Object | `Email`, `OtpCode`, `Password`, `JwtToken`, `OtpEmbedded` | Encapsula validaciones de negocio en el propio tipo |
| Adapter | `UserServiceFeignAdapter`, `EmailSenderAdapter`, `RefreshTokenRepositoryAdapter` | Conecta puertos de dominio con tecnologías externas |
| Repository (caché) | `RefreshTokenRepositoryAdapter`, `OtpRedisRepository`, etc. | Abstrae el almacenamiento Redis del dominio |
| Global Exception Handler | `GlobalExceptionHandler` (@RestControllerAdvice) | Centraliza el mapeo de excepciones de dominio a códigos HTTP |

### Módulos que Consume / Produce

| Módulo | Dato que consume/produce | Cómo lo obtiene | Impacto si falla |
|---|---|---|---|
| M02 — User Service | `userId`, `email`, `passwordHash`, `verified` | HTTP REST via OpenFeign (`/api/v1/internal/users/*`) | `GlobalExceptionHandler` retorna 503 o 404 según el status Feign |
| Servicio de Email / Notificaciones | Evento `OtpVerificationEventDto` o `PasswordResetEventDto` | Publica en RabbitMQ `auth.exchange` | `EmailSenderAdapter` loguea el OTP en consola como fallback; el flujo no se interrumpe |
| Redis | OTPs, refresh tokens, bloqueos de cuenta | Spring Data Redis (`@RedisHash`) | Sin Redis el servicio no arranca |

### Diagramas de Secuencia

Un diagrama de secuencia es un tipo de diagrama UML que muestra, en orden temporal, cómo interactúan los actores y los componentes del sistema mediante mensajes o llamadas.

### 1. Inicializar Verificación (Init Verification)

Muestra el flujo de inicialización: generación de OTP de 6 dígitos, almacenamiento en cache (Redis) y envío al correo institucional.

![INITVerificacion](src/main/resources/INITVerificacion.png)

### 2. Verificar OTP (Verify OTP)

Describe la validación del código OTP, la activación de la cuenta en el User Service y la generación de tokens JWT (access + refresh).

![VerifyOTP](src/main/resources/VerifyOTP.png)

### 3. Reenviar OTP (Resend OTP)

Muestra el proceso para generar y reenviar un nuevo OTP cuando el anterior ha expirado o se agotaron los 3 intentos de validación.

![ResendOTP](src/main/resources/ResendOTP.png)

### 4. Login

Representa el inicio de sesión con validación de email y contraseña, verificación del estado de la cuenta, manejo de bloqueos tras intentos fallidos y emisión de tokens.

![Login](src/main/resources/Longin.png)

### 5. Refresh Token

Ilustra la rotación de tokens: validación del refresh token, invalidación del anterior y emisión de un nuevo par (access + refresh).

![RefreshToken](src/main/resources/RefreshToken.png)

### 6. Logout

Explica el cierre de sesión mediante la extracción del userId del Bearer token y la eliminación del refresh token activo en Redis.

![Logout](src/main/resources/Logout.png)

### 7. Forgot Password

Describe la solicitud de recuperación de contraseña: búsqueda del usuario, generación del código de recuperación de 6 dígitos y envío al correo.

![ForgotPassword](src/main/resources/ForgotPassword.png)

### 8. Reset Password

Representa la validación del código de recuperación, la actualización de la contraseña (hasheada con BCrypt) y la eliminación del código usado.

![ResetPassword](src/main/resources/ResetPassword.png)

### 9. Change Password

Muestra el cambio de contraseña para un usuario autenticado: extracción del userId del Bearer token, validación de la contraseña actual y actualización con la nueva.

![ChangePassword](src/main/resources/ChangePassword.png)

---

## 5. Diagrama de Datos

Este módulo **no persiste datos en una base de datos relacional**. Toda la persistencia es en **Redis** mediante entidades `@RedisHash`. A continuación se muestra el modelo de datos en caché:

<!-- ============================================================ -->
<!-- 📸 IMAGEN PENDIENTE: agregar diagrama del modelo Redis       -->
<!-- Ruta esperada: src/main/resources/DiagramaDatos.png          -->
<!-- Contenido sugerido: diagrama con las 4 entidades Redis       -->
<!--   OtpCache, PasswordResetOtpCache, LockoutCache,            -->
<!--   RefreshTokenCache — con sus campos y TTLs                  -->
<!-- ============================================================ -->

<div align="center">
<img src="src/main/resources/DiagramaDatos.png" alt="Diagrama de Datos — Modelo Redis" width="700"/>
</div>

### Entidades Redis (`@RedisHash`)

#### `OtpCache` — TTL: 600 s (10 min)

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `String` | Email del usuario — clave del hash |
| `otpCode` | `String` | Código OTP de 6 dígitos generado con SecureRandom |
| `attempts` | `int` | Contador de intentos fallidos (máx. 3) |
| `used` | `boolean` | Si el OTP ya fue consumido |

#### `PasswordResetOtpCache` — TTL: 600 s (10 min)

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `String` | Email del usuario — clave del hash |
| `code` | `String` | Código de recuperación de 6 dígitos |
| `userId` | `String` | UUID del usuario propietario |
| `used` | `boolean` | Si el código ya fue consumido |

#### `LockoutCache` — TTL: ~30 min

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | `String` | Email del usuario — clave del hash |
| `failedAttempts` | `int` | Número de intentos fallidos de login (bloqueo a los 5) |
| `locked` | `boolean` | Si la cuenta está bloqueada |

#### `RefreshTokenCache` — TTL: 7 días

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
<img src="src/main/resources/DiagramaClases.png" alt="Diagrama de Clases" width="700"/>
</div>

**Descripción del diagrama:** Muestra las capas de dominio y aplicación: las entidades `User` y `RefreshToken`, sus value objects (`Email`, `OtpCode`, `Password`, `JwtToken`, `OtpEmbedded`), los nueve puertos de entrada (uno por use case), los tres puertos de salida (`UserServicePort`, `EmailSenderPort`, `RefreshTokenRepositoryPort`), y las implementaciones de use case que los conectan.

### Clases principales del dominio

| Clase | Tipo | Responsabilidad |
|---|---|---|
| `User` | Model | Entidad de dominio del usuario — contiene estado de verificación, lockout y OTPs |
| `RefreshToken` | Model | Entidad de sesión — contiene el par JWT/refresh token y su estado de revocación |
| `Email` | Value Object | Encapsula y valida el formato del email |
| `OtpCode` | Value Object | Valida que el OTP sea exactamente 6 dígitos numéricos |
| `OtpEmbedded` | Value Object | OTP con timestamp de generación y estado de uso |
| `Password` | Value Object | Valida la complejidad mínima de contraseña |
| `JwtToken` | Value Object | Encapsula el string del JWT y su extracción de claims |
| `RolEnum` | Enum | Roles del sistema (STUDENT, ADMIN, etc.) |
| `Genero` | Enum | Géneros disponibles en el perfil de usuario |
| `Interes` | Enum | Intereses disponibles en el perfil de usuario |
| `ProfileVisibility` | Enum | Visibilidad del perfil (PUBLIC, PRIVATE, etc.) |
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

### General

<div align="center">
<img src="src/main/resources/componetes generales.png" alt="Diagrama de Componentes General" width="700"/>
</div>

### Específico

<div align="center">
<img src="src/main/resources/componentes especificos.png" alt="Diagrama de Componentes Específico" width="700"/>
</div>

**Descripción:** Muestra los bloques funcionales del módulo: `AuthController` como punto de entrada REST, los nueve use cases de aplicación, los tres adaptadores de salida (`UserServiceFeignAdapter` → User Service HTTP, `EmailSenderAdapter` → RabbitMQ, `RefreshTokenRepositoryAdapter` → Redis), y las cuatro entidades de caché Redis.

| Componente | Tipo | Interfaces que expone |
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

## 8. Funcionalidades del Módulo

| ID | Funcionalidad | RF asociado | Descripción |
|---|---|---|---|
| F01 | Inicializar verificación OTP | RF-01 | Genera OTP de 6 dígitos con SecureRandom, lo guarda en Redis (TTL 10 min) y publica evento en RabbitMQ. Llamado por el Registration Service tras crear el usuario. |
| F02 | Verificar OTP y activar cuenta | RF-01 | Valida el OTP (máx. 3 intentos). Si es correcto, activa la cuenta en User Service y retorna JWT + refresh token. |
| F03 | Reenviar OTP | RF-01 | Genera y publica un nuevo OTP. Usar cuando el anterior expiró (10 min) o se agotaron los 3 intentos. |
| F04 | Login | RF-03 | Autentica con email y contraseña BCrypt. Bloquea la cuenta tras 5 intentos fallidos (~30 min en Redis). Retorna JWT (15 min) + refresh token (7 días). |
| F05 | Renovar access token | RF-03 | Intercambia un refresh token válido por un nuevo par JWT/refresh token (rotación). El refresh token anterior se invalida. |
| F06 | Logout | RF-03 | Requiere Bearer JWT. Extrae `userId` del token y elimina el refresh token de Redis, cerrando la sesión. |
| F07 | Olvidé mi contraseña | RF-07 | Busca el usuario en User Service por email y publica evento de código de recuperación de 6 dígitos en RabbitMQ (TTL 10 min). |
| F08 | Restablecer contraseña | RF-07 | Valida el código de recuperación de uso único, hashea la nueva contraseña con BCrypt y la actualiza en User Service via Feign. |
| F09 | Cambiar contraseña (autenticado) | RF-09 | Requiere Bearer JWT. Verifica la contraseña actual contra el hash en User Service, luego actualiza con la nueva contraseña hasheada. |

---

## 9. Endpoints Expuestos

### F01 — Inicializar Verificación OTP

**Endpoint:** `POST /api/v1/auth/init-verification`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional del usuario recién registrado |
| `hashedPassword` | `String` | body | Sí | Contraseña ya hasheada con BCrypt (viene del Registration Service) |

#### Ejemplo

```
POST /api/v1/auth/init-verification
```

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 400 | Campos faltantes o inválidos | `VALIDATION_ERROR` |

---

### F02 — Verificar OTP

**Endpoint:** `POST /api/v1/auth/verify-otp`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional del usuario |
| `otp` | `String` | body | Sí | Código OTP de 6 dígitos recibido por correo |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 422 | OTP inválido, ya usado o expirado | `OTP_INVALID` / `OTP_EXPIRED` |
| 429 | 3 intentos fallidos agotados | `OTP_MAX_ATTEMPTS` |

---

### F03 — Reenviar OTP

**Endpoint:** `POST /api/v1/auth/resend-otp`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional del usuario cuyo OTP expiró o se agotaron sus intentos |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 422 | No existe cuenta con ese email | `OTP_INVALID` |

---

### F04 — Login

**Endpoint:** `POST /api/v1/auth/login`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional del usuario |
| `password` | `String` | body | Sí | Contraseña en texto plano (se compara con hash BCrypt en User Service) |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 401 | Contraseña incorrecta o usuario no encontrado | `INVALID_CREDENTIALS` |
| 403 | Email no verificado (OTP pendiente) | `EMAIL_NOT_VERIFIED` |
| 422 | Cuenta bloqueada por 5 intentos fallidos | `ACCOUNT_LOCKED` |

---

### F05 — Renovar Access Token (Rotación)

**Endpoint:** `POST /api/v1/auth/refresh`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `refreshToken` | `String` | body | Sí | UUID del refresh token activo obtenido en login o verify-otp |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 401 | Refresh token inválido o expirado | `TOKEN_INVALID` / `TOKEN_EXPIRED` |

---

### F06 — Logout

**Endpoint:** `POST /api/v1/auth/logout`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `Authorization` | `String` | header | Sí | Token JWT en formato `Bearer <accessToken>` |

#### Ejemplo

```
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

```json
// Response
{ "message": "Session closed successfully" }
```

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 401 | JWT ausente o inválido | `TOKEN_INVALID` |

---

### F07 — Olvidé Mi Contraseña

**Endpoint:** `POST /api/v1/auth/forgot-password`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional de la cuenta a recuperar |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 422 | No existe cuenta con ese email | `OTP_INVALID` |

---

### F08 — Restablecer Contraseña

**Endpoint:** `POST /api/v1/auth/reset-password`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `email` | `String` | body | Sí | Email institucional de la cuenta |
| `otp` | `String` | body | Sí | Código de recuperación de 6 dígitos recibido por correo |
| `newPassword` | `String` | body | Sí | Nueva contraseña (se hasheará con BCrypt antes de guardar) |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 422 | Código inválido, ya usado o expirado | `OTP_INVALID` / `OTP_EXPIRED` |

---

### F09 — Cambiar Contraseña (Autenticado)

**Endpoint:** `POST /api/v1/auth/change-password`

#### Request

| Campo | Tipo | Origen | Obligatorio | Descripción |
|---|---|---|---|---|
| `Authorization` | `String` | header | Sí | Token JWT en formato `Bearer <accessToken>` |
| `currentPassword` | `String` | body | Sí | Contraseña actual del usuario para verificación |
| `newPassword` | `String` | body | Sí | Nueva contraseña a establecer |

#### Ejemplo

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

#### Errores manejados

| Código HTTP | Escenario | Código de error |
|---|---|---|
| 401 | Contraseña actual incorrecta o JWT inválido | `INVALID_CREDENTIALS` / `TOKEN_INVALID` |

---

### Resumen de todos los Endpoints

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

Si RabbitMQ no está disponible, `EmailSenderAdapter` captura la excepción y loguea el OTP/código en consola (`INFO`). El flujo de negocio **no se interrumpe** — el usuario puede obtener el código de los logs del servidor en desarrollo.

---

## 11. Evidencia de Pruebas Unitarias

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
# Ejecutar todas las pruebas (requiere Redis en localhost:6379)
./mvnw test

# Ejecutar una prueba específica
./mvnw test -Dtest=LoginUseCaseTest

# Ejecutar todas las pruebas + reporte JaCoCo + verificar cobertura ≥ 80%
./mvnw verify
```

### Captura — Resultado de ejecución

<div align="center">
<img src="src/main/resources/unitarias.png" alt="Evidencia pruebas unitarias" width="700"/>
</div>

---

## 12. Evidencia de Análisis de Cobertura

### Generar el reporte

```bash
./mvnw clean test jacoco:report
# Reporte HTML: target/site/jacoco/index.html
# Reporte XML:  target/site/jacoco/jacoco.xml  (consumido por SonarCloud)
```

### Captura — Reporte JaCoCo

<div align="center">
<img src="src/main/resources/jacoco.png" alt="Reporte de cobertura JaCoCo" width="700"/>
</div>

### Métricas objetivo

| Métrica | Objetivo | Obtenido |
|---|---|---|
| Cobertura de instrucciones | ≥ 80% | 97% |
| Cobertura de ramas | ≥ 60% | 93% |
| Clases cubiertas | — | 29 de 29 |

---

## 13. Cómo Ejecutar el Proyecto

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker & Docker Compose
- Una instancia de Redis accesible
- RabbitMQ / CloudAMQP (opcional en local — si no hay, el OTP aparece en los logs)
- URL del User Service (M02) configurada

### Modos de ejecución

| Modo | Comando | Cache | Puerto | Swagger |
|---|---|---|---|---|
| Local (perfil dev) | `./mvnw spring-boot:run` | Redis en `localhost:6379` | 8080 | `http://localhost:8080/swagger-ui.html` |
| Docker Compose | `docker compose up --build` | Redis + RabbitMQ en contenedores | 9090 | `http://localhost:9090/swagger-ui.html` |

### Opción 1 — Ejecución local

```bash
# 1. Clonar el repositorio
git clone https://github.com/PATRICI-A/snorlax-energy-auth-service.git
cd snorlax-energy-auth-service

# 2. Levantar Redis localmente (si no lo tienes)
docker run -d -p 6379:6379 redis:7-alpine

# 3. Ejecutar (perfil dev activo por defecto)
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.
Swagger UI disponible en `http://localhost:8080/swagger-ui.html` (también en la raíz `/`).

### Opción 2 — Docker Compose (recomendado)

```bash
# Levanta auth-service + Redis + RabbitMQ + SonarQube
docker compose up --build

# Solo auth-service + Redis (más liviano para desarrollo)
docker compose up auth-service redis

# Ver logs en tiempo real
docker compose logs -f auth-service

# Detener y eliminar volúmenes
docker compose down -v
```

### Variables de entorno

| Variable | Valor por defecto (dev) | Descripción |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil activo de Spring Boot |
| `JWT_SECRET` | `dev-secret-key-must-be-at-least-32-characters` | Clave HMAC-SHA256 compartida con todos los módulos (mín. 32 chars) |
| `JWT_EXPIRATION` | `900000` | Duración del access token en milisegundos (15 min) |
| `REDIS_HOST` | `localhost` | Host de Redis |
| `REDIS_PORT` | `6379` | Puerto de Redis |
| `REDIS_PASSWORD` | _(vacío)_ | Contraseña de Redis (usar con Upstash/Azure Redis en producción) |
| `REDIS_SSL` | `false` | Activar TLS para Redis (`true` en producción) |
| `RABBITMQ_HOST` | `woodpecker.rmq.cloudamqp.com` | Host de CloudAMQP |
| `RABBITMQ_PORT` | `5671` | Puerto AMQP con SSL |
| `RABBITMQ_USERNAME` | `thjdybjd` | Usuario CloudAMQP |
| `RABBITMQ_PASSWORD` | _(secreto)_ | Contraseña CloudAMQP |
| `RABBITMQ_VIRTUAL_HOST` | `thjdybjd` | Virtual host CloudAMQP |
| `RABBITMQ_SSL` | `true` | TLS para RabbitMQ (`true` con CloudAMQP) |
| `USER_SERVICE_URL` | `http://localhost:8081` | URL base del User Service (M02) |
| `PORT` | `8080` | Puerto del servidor (Azure usa este valor via `server.port`) |
| `SERVER_URL` | `http://localhost:8080` | URL base mostrada en Swagger servers |

---

## 14. Evidencia del Despliegue CI/CD

<div align="center">
<img src="src/main/resources/evidencia despligue.png" alt="Evidencia de despliegue GitHub Actions" width="700"/>
</div>

<div align="center">
<img src="src/main/resources/despliegue.png" alt="Despliegue en Azure App Service" width="700"/>
</div>

---

## 15. Link Swagger Desplegado

| Ambiente | URL |
|---|---|
| **Producción (Azure)** | `https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/swagger-ui/index.html` |
| **Local** | `http://localhost:8080/swagger-ui.html` |
| **Docker Compose** | `http://localhost:9090/swagger-ui.html` |
| **OpenAPI JSON** | `https://patricia-etfgcpfsb5g2aqby.canadacentral-01.azurewebsites.net/v3/api-docs` |

---

## 16. Estructura del Código

```
snorlax-energy-auth-service/
│
├── src/
│   ├── main/
│   │   ├── java/edu/eci/patricia/DOSW_patricia/
│   │   │   │
│   │   │   ├── domain/                              # CAPA DE DOMINIO
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java                    # Entidad de usuario (verified, lockout, otp)
│   │   │   │   │   └── RefreshToken.java            # Entidad de sesión JWT
│   │   │   │   ├── ports/
│   │   │   │   │   ├── in/                          # Puertos de entrada (contratos de use cases)
│   │   │   │   │   │   ├── LoginPort.java
│   │   │   │   │   │   ├── InitVerificationPort.java
│   │   │   │   │   │   ├── ValidateOtpPort.java
│   │   │   │   │   │   ├── ResendOtpPort.java
│   │   │   │   │   │   ├── LogoutPort.java
│   │   │   │   │   │   ├── RefreshTokenPort.java
│   │   │   │   │   │   ├── ForgotPasswordPort.java
│   │   │   │   │   │   ├── ResetPasswordPort.java
│   │   │   │   │   │   └── ChangePasswordPort.java
│   │   │   │   │   └── out/                         # Puertos de salida (contratos de infraestructura)
│   │   │   │   │       ├── UserServicePort.java
│   │   │   │   │       ├── EmailSenderPort.java
│   │   │   │   │       └── RefreshTokenRepositoryPort.java
│   │   │   │   ├── exceptions/                      # Excepciones de dominio tipadas
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
│   │   │   │   └── valueobjects/                    # Value Objects con validación integrada
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
│   │   │   │   ├── usecase/                         # Implementaciones de puertos de entrada
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
│   │   │   │       ├── request/                     # DTOs de entrada de la capa de aplicación
│   │   │   │       ├── response/                    # LoginResponseDto, RegisterResponseDto
│   │   │   │       └── external/
│   │   │   │           └── UserDto.java             # Record DTO del User Service
│   │   │   │
│   │   │   ├── infrastructure/                      # CAPA DE INFRAESTRUCTURA
│   │   │   │   ├── adapters/
│   │   │   │   │   ├── adapter/                     # Adaptadores de puertos de salida
│   │   │   │   │   │   ├── UserServiceFeignAdapter.java
│   │   │   │   │   │   └── RefreshTokenRepositoryAdapter.java
│   │   │   │   │   └── cache/
│   │   │   │   │       ├── entity/                  # Entidades @RedisHash
│   │   │   │   │       │   ├── OtpCache.java              # TTL 600 s
│   │   │   │   │       │   ├── PasswordResetOtpCache.java # TTL 600 s
│   │   │   │   │       │   ├── LockoutCache.java
│   │   │   │   │       │   └── RefreshTokenCache.java     # TTL 7 días
│   │   │   │   │       └── repository/              # Spring Data Redis repositories
│   │   │   │   │           ├── OtpRedisRepository.java
│   │   │   │   │           ├── PasswordResetOtpRedisRepository.java
│   │   │   │   │           ├── LockoutRedisRepository.java
│   │   │   │   │           └── RefreshTokenRedisRepository.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── SecurityConfig.java          # Stateless, BCrypt, CORS, rutas públicas
│   │   │   │   │   └── RabbitMQConfig.java          # TopicExchange, RabbitTemplate, RabbitAdmin
│   │   │   │   └── external/
│   │   │   │       ├── JwtService.java              # Generación y validación HMAC-SHA256
│   │   │   │       ├── EmailSenderAdapter.java      # Publica eventos en RabbitMQ
│   │   │   │       ├── UserServiceFeignClient.java  # @FeignClient al User Service
│   │   │   │       └── dto/
│   │   │   │           ├── OtpVerificationEventDto.java
│   │   │   │           └── PasswordResetEventDto.java
│   │   │   │
│   │   │   ├── entrypoints/                         # CAPA DE ENTRADA
│   │   │   │   ├── rest/
│   │   │   │   │   ├── controller/
│   │   │   │   │   │   └── AuthController.java      # 9 endpoints POST /api/v1/auth/**
│   │   │   │   │   ├── mapper/
│   │   │   │   │   │   └── AuthRestMapper.java      # MapStruct: Request → DTO de aplicación
│   │   │   │   │   └── request/                     # Request bodies con validación @Valid
│   │   │   │   └── advice/
│   │   │   │       ├── GlobalExceptionHandler.java  # @RestControllerAdvice (10+ excepciones)
│   │   │   │       └── ErrorResponse.java           # DTO de error estándar {code, message, detail}
│   │   │   │
│   │   │   └── DoswPatriciaApplication.java         # Main — @SpringBootApplication @EnableFeignClients
│   │   │
│   │   └── resources/
│   │       ├── application.yml                      # Config base (activa perfil desde $SPRING_PROFILES_ACTIVE)
│   │       ├── application-dev.yml                  # Config dev: Redis, RabbitMQ CloudAMQP, JWT, Feign, Swagger
│   │       ├── DiagramaClases.png
│   │       ├── DiagramaDatos.png                    # ← agregar esta imagen (modelo Redis)
│   │       ├── componentes especificos.png
│   │       ├── componetes generales.png
│   │       ├── despliegue.png
│   │       ├── evidencia despligue.png
│   │       ├── jacoco.png
│   │       └── unitarias.png
│   │
│   └── test/
│       └── java/edu/eci/patricia/DOSW_patricia/
│           ├── application/usecase/      # 9 clases de prueba — una por use case
│           ├── domain/model/             # UserTest, RefreshTokenTest
│           ├── domain/valueobjects/      # EmailTest, OtpCodeTest, PasswordTest, JwtTokenTest, OtpEmbeddedTest
│           ├── entrypoints/advice/       # GlobalExceptionHandlerTest
│           ├── entrypoints/rest/         # AuthControllerTest
│           └── infrastructure/external/ # JwtServiceTest
│
├── .github/workflows/
│   ├── ci.yml           # CI — Build & Test con Redis, JaCoCo, Surefire, JAR artifact
│   ├── cd.yml           # CD — Deploy JAR a Azure App Service (post CI en main)
│   └── sonar.yml        # QA — SonarCloud analysis (push/PR a main/develop)
├── Dockerfile           # Multi-stage: Maven 3.9 JDK21 alpine → JRE alpine, EXPOSE 8080
├── docker-compose.yml   # auth-service + Redis + RabbitMQ + SonarQube + sonar-db
├── DEPLOYMENT_AZURE.md  # Guía completa de configuración en Azure App Service
├── pom.xml
└── README.md
```

---

## 17. Código Documentado

Todos los endpoints del `AuthController` están documentados con anotaciones OpenAPI:

- `@Tag(name = "Authentication", description = "...")` — categoría del controlador en Swagger UI
- `@Operation(summary = "...", description = "...")` — propósito y comportamiento de cada endpoint
- `@ApiResponses({@ApiResponse(responseCode = "...", description = "...")})` — todos los códigos de respuesta posibles
- `@SecurityRequirement(name = "bearerAuth")` — marca los endpoints que requieren JWT Bearer (`/logout`, `/change-password`)

Los use cases documentan únicamente lógica no obvia mediante comentarios en línea (constantes de negocio: `MAX_FAILED_ATTEMPTS = 5`, `MAX_ATTEMPTS = 3`). El `Dockerfile`, `pom.xml` y `docker-compose.yml` contienen comentarios explicando decisiones de infraestructura (multi-stage build, caché de capas Docker, exclusiones de JaCoCo).

Acceso a la documentación interactiva:
- **Swagger UI:** `/swagger-ui.html` (también en la raíz `/` por `use-root-path: true`)
- **OpenAPI JSON:** `/v3/api-docs`

---

## 18. Conexiones con Servicios Externos

| Servicio | Tipo | Configuración | Propósito | Manejo de fallo |
|---|---|---|---|---|
| **Redis** | Caché en memoria | `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_SSL` | OTPs (TTL 10 min), refresh tokens (TTL 7 días), bloqueos de cuenta | Sin Redis el servicio no arranca |
| **RabbitMQ (CloudAMQP)** | Broker AMQP | `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `RABBITMQ_SSL` | Publicación de eventos `auth.otp.verification` y `auth.password.reset` | `EmailSenderAdapter` loguea el código en consola — el flujo no se interrumpe |
| **User Service (M02)** | HTTP REST via OpenFeign | `USER_SERVICE_URL` | Buscar usuario por email/id, marcar como verificado, actualizar contraseña | `GlobalExceptionHandler` captura `FeignException`: `404` → 404, otros → 503 |
| **SonarCloud** | Análisis estático | `SONAR_TOKEN` (GitHub secret) | Cobertura y calidad de código en pipeline `sonar.yml` | Solo afecta el pipeline de QA, no la ejecución del servicio |
| **Azure App Service** | Cloud hosting | `AZURE_CREDENTIALS`, `AZURE_APP_SERVICE_NAME` (GitHub secrets) | Despliegue del JAR en producción via `azure/webapps-deploy@v3` | El pipeline CD falla y notifica; no hay impacto en el servicio ya desplegado |

---

## 19. Pipeline de Desarrollo

El pipeline `.github/workflows/ci.yml` se ejecuta en cada push a `main` o `develop` y en cada PR hacia esas ramas.

### Pasos del pipeline

| Paso | Acción | Descripción |
|---|---|---|
| 1 | Checkout | `actions/checkout@v4` con `fetch-depth: 0` (requerido por SonarCloud) |
| 2 | Setup JDK 21 | `actions/setup-java@v4` distribución Temurin, caché Maven |
| 3 | Redis service | Contenedor `redis:7-alpine` con healthcheck `redis-cli ping` |
| 4 | Build & Test | `mvn clean verify -B` — compila, tests, JaCoCo. Falla si cobertura < 80% |
| 5 | Upload JaCoCo | Artefacto `jacoco-report` — `target/site/jacoco/` (retención 14 días) |
| 6 | Upload Surefire | Artefacto `surefire-reports` — `target/surefire-reports/` (retención 14 días) |
| 7 | Upload JAR | Artefacto `auth-service-jar` — `target/auth-service-*.jar` (retención 14 días) |

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

### Pipeline QA — SonarCloud (`.github/workflows/sonar.yml`)

Se ejecuta en paralelo con el CI. Corre `mvn clean verify` para generar el reporte JaCoCo XML y lo envía a SonarCloud via `SonarSource/sonarcloud-github-action@v3`.

**Secrets requeridos:** `SONAR_TOKEN`, `GITHUB_TOKEN`

---

## 20. Pipeline de Producción

El pipeline `.github/workflows/cd.yml` se activa automáticamente cuando el pipeline CI completa exitosamente en la rama `main` (`workflow_run` trigger).

### Pasos del pipeline

| Paso | Acción | Descripción |
|---|---|---|
| 1 | Checkout | `actions/checkout@v4` |
| 2 | Setup JDK 21 | `actions/setup-java@v4` con caché Maven |
| 3 | Build JAR | `mvn package -DskipTests -B` — genera el JAR de producción |
| 4 | Azure Login | `azure/login@v2` usando `AZURE_CREDENTIALS` |
| 5 | Deploy JAR | `azure/webapps-deploy@v3` — despliega `target/auth-service-*.jar` en App Service |
| 6 | Show URL | Loguea la URL de la API y Swagger desplegados |

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

**Secrets requeridos en GitHub:**

| Secret | Descripción |
|---|---|
| `AZURE_CREDENTIALS` | JSON del Service Principal (clientId, clientSecret, tenantId, subscriptionId) |
| `AZURE_APP_SERVICE_NAME` | Nombre del App Service en Azure (ej. `app-patricia-auth`) |
| `JWT_SECRET_TEST` | Clave JWT para el entorno de CI (≥ 32 caracteres) |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |

Ver la guía completa de configuración en [DEPLOYMENT_AZURE.md](DEPLOYMENT_AZURE.md).

### Captura — Pipeline de producción

<div align="center">
<img src="src/main/resources/despliegue.png" alt="Pipeline de producción exitoso" width="700"/>
</div>

---

## 21. Dockerizado

### Dockerfile (Multi-stage Build)

```dockerfile
# ── Stage 1: Build ────────────────────────────────────────────────────────────
# maven:3.9-eclipse-temurin-21-alpine incluye Maven + JDK 21
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia pom.xml primero → las dependencias se cachean como capa separada
# Mientras pom.xml no cambie, Docker reutiliza esta capa aunque el código sí cambie
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

### Docker Compose

El `docker-compose.yml` levanta los siguientes servicios:

| Servicio | Imagen | Puertos | Descripción |
|---|---|---|---|
| `auth-service` | Build local | `9090:9090` | El microservicio de autenticación |
| `redis` | `redis:7-alpine` | `6379:6379` | Caché de sesiones, OTPs y bloqueos |
| `rabbitmq` | `rabbitmq:3-management-alpine` | `5672:5672`, `15672:15672` | Broker de mensajería (UI en :15672) |
| `sonarqube` | `sonarqube:community` | `9000:9000` | Análisis de calidad local |
| `sonar-db` | `postgres:16` | interno | BD de SonarQube |

```bash
# Levantar todos los servicios
docker compose up --build

# Solo auth-service + Redis (más liviano para desarrollo)
docker compose up auth-service redis

# Ver logs en tiempo real
docker compose logs -f auth-service

# Detener y eliminar volúmenes
docker compose down -v
```

> **Nota:** `docker-compose.yml` expone el puerto `9090` localmente. Para despliegue en Azure (CD pipeline) se usa el `Dockerfile` directamente y Azure enruta al puerto `8080`.

---

## 22. Estrategia de Versionamiento

### Estrategia de Ramas (Git Flow)

| Rama | Propósito | Reglas |
|---|---|---|
| `main` | Versión estable lista para producción | Recibe merges desde `feature/*` o `develop`. Cada merge exitoso activa el pipeline CD hacia Azure. Protegida con PR obligatorio. |
| `develop` | Integración continua de trabajo | Recibe merges desde `feature/*`. Activa el pipeline CI. |
| `feature/*` | Desarrollo de funcionalidades | Base: `develop`. Se fusiona a `develop` mediante PR revisado. |

### Convenciones de ramas

```
feature/[nombre-funcionalidad]
hotfix/[descripcion-del-fix]
release/[version]
```

### Convenciones de commits (Conventional Commits)

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
