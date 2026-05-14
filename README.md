# Auth Service — M01 Identidad

**PATRICIA** · Red Social Universitaria · Escuela Colombiana de Ingeniería Julio Garavito

---

## Integrantes

| Nombre | Rol |
|---|---|
| Sebastian Castillejo | Desarrollador |
| Juan Melo | Desarrollador |
| Samuel Gil | Desarrollador |
| Maria Jose | Desarrolladora |

---

## Tecnologías utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.6 | Framework base |
| Spring Security | (managed) | Seguridad y CORS |
| Spring Data Redis | (managed) | Cache de OTPs y refresh tokens |
| Spring AMQP / RabbitMQ | (managed) | Cola de mensajería para emails |
| JJWT | 0.12.6 | Generación y validación de tokens JWT |
| MapStruct | 1.6.3 | Mapeo entre capas (DTOs ↔ dominio) |
| Lombok | 1.18.36 | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.6 | Documentación Swagger UI |
| JaCoCo | 0.8.12 | Análisis de cobertura de pruebas |
| Docker | — | Contenedorización |
| GitHub Actions | — | Pipeline CI/CD |
| Amazon ECR + Lambda | — | Despliegue en producción |

---

## Descripción del módulo

El **Auth Service** es el microservicio encargado de la **identidad y autenticación** dentro de la plataforma PATRICIA. Gestiona el ciclo de vida completo de la sesión de un usuario: desde la verificación del correo institucional mediante OTP hasta el manejo seguro de tokens JWT y la recuperación de contraseña.

Cubre los requisitos funcionales:

- **RF01 – Registro e Identidad**: permite que un estudiante active su cuenta verificando su correo institucional `@escuelaing.edu.co` mediante un código OTP de 6 dígitos enviado por correo.
- **RF03 – Autenticación Segura**: permite que los usuarios registrados accedan con credenciales seguras, recibiendo tokens JWT de acceso y refresh, con bloqueo temporal tras intentos fallidos repetidos.

---

## Cómo funciona el módulo

### Flujo de registro y verificación (RF01)

1. El servicio de usuarios (User Service) crea el usuario y llama a `POST /api/v1/auth/init-verification` con el email institucional y la contraseña ya hasheada con BCrypt.
2. El Auth Service genera un OTP de 6 dígitos, lo almacena en Redis con TTL de 10 minutos y publica un evento en RabbitMQ.
3. El servicio de notificaciones consume el evento y envía el correo al usuario.
4. El usuario llama a `POST /api/v1/auth/verify-otp` con el código recibido.
5. Si el OTP es válido, el Auth Service marca la cuenta como verificada en el User Service (llamada HTTP) y devuelve un par de tokens JWT.

### Flujo de autenticación (RF03)

1. El usuario llama a `POST /api/v1/auth/login` con email y contraseña.
2. El Auth Service consulta el User Service para obtener los datos del usuario y valida la contraseña con BCrypt.
3. Si es correcta, genera un **access token** (15 min) y un **refresh token** (7 días), almacenando el refresh token en Redis.
4. Tras 5 intentos fallidos consecutivos la cuenta se bloquea 30 minutos (bloqueo almacenado en Redis).
5. El refresh token implementa **rotación**: cada vez que se usa se invalida y se emite un nuevo par.

### Módulos con los que se comunica

| Módulo | Protocolo | Dirección |
|---|---|---|
| User Service | HTTP REST (`RestClient`) | Saliente — consulta y actualización de usuarios |
| Notification Service | RabbitMQ (`auth.exchange`) | Saliente — eventos de email |

### Patrones y estilo de arquitectura

- **Arquitectura hexagonal (Ports & Adapters)**: el dominio no tiene dependencias hacia el exterior; se comunica únicamente a través de puertos (`in` para casos de uso, `out` para repositorios y servicios externos).
- **Patrón Use Case**: cada operación de negocio está encapsulada en su propio caso de uso (`LoginUseCase`, `ValidateOtpUseCase`, etc.).
- **Value Objects**: `Email`, `Password`, `JwtToken`, `OtpCode` y `OtpEmbedded` encapsulan las reglas de validación del dominio.
- **Patrón Adapter**: los repositorios Redis y el cliente HTTP al User Service son adaptadores que implementan los puertos de salida.
- **Event-driven (mensajería)**: el envío de emails es asíncrono; el Auth Service publica eventos en RabbitMQ y no espera respuesta.

### Estructura de capas

```
src/main/java/.../
├── domain/
│   ├── model/          # Entidades de dominio (User, RefreshToken)
│   ├── valueobjects/   # Value objects con validación propia
│   ├── ports/
│   │   ├── in/         # Interfaces de los casos de uso
│   │   └── out/        # Interfaces de repositorios y servicios externos
│   └── exceptions/     # Excepciones de negocio
├── application/
│   ├── usecase/        # Implementaciones de los casos de uso
│   ├── dto/            # DTOs de request/response de la capa de aplicación
│   └── mapper/         # Mapeos entre capas
├── infrastructure/
│   ├── adapters/
│   │   ├── adapter/    # Adaptadores de repositorio (Redis)
│   │   └── cache/      # Entidades y repositorios de Redis
│   ├── external/       # JwtService, EmailSenderAdapter, cliente User Service
│   └── config/         # SecurityConfig, RabbitMQConfig, RestClientConfig
└── entrypoints/
    ├── rest/
    │   ├── controller/ # AuthController (REST)
    │   ├── request/    # Request bodies de la API REST
    │   └── mapper/     # Mapeo REST request → DTO de aplicación
    └── advice/         # GlobalExceptionHandler
```

---

## Diagramas de datos

<!-- Insertar diagrama de datos aquí -->

---

## Diagramas de Secuencia

Un diagrama de secuencia es un tipo de diagrama UML que muestra, en orden temporal, cómo interactúan los actores y los componentes del sistema mediante mensajes o llamadas.

Es importante porque permite entender el comportamiento dinámico de cada funcionalidad, validar los flujos antes de implementar, detectar errores de lógica y comunicar claramente cómo se procesa cada solicitud de extremo a extremo.

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

## Diagramas de clases

![alt text](src/main/resources/DiagramaClases.png)

---

## Diagrama de componentes

## GENERAL

![alt text](<src/main/resources/componetes generales.png>)

## ESPECIFICO

![alt text](<src/main/resources/componentes especificos.png>)

---

## Funcionalidades

| # | Funcionalidad | Descripción |
|---|---|---|
| 1 | Iniciar verificación OTP | Genera y envía un OTP de 6 dígitos al correo institucional del usuario |
| 2 | Verificar OTP | Valida el OTP y activa la cuenta; devuelve tokens JWT |
| 3 | Reenviar OTP | Genera un nuevo OTP cuando el anterior expiró o se agotaron los intentos |
| 4 | Login | Autentica con email y contraseña; devuelve access + refresh token |
| 5 | Refresh token | Rota el refresh token y emite un nuevo par de tokens |
| 6 | Logout | Invalida la sesión eliminando el refresh token de Redis |
| 7 | Olvidé mi contraseña | Envía un código de recuperación de 6 dígitos al correo |
| 8 | Restablecer contraseña | Valida el código de recuperación y actualiza la contraseña |
| 9 | Cambiar contraseña | Permite a un usuario autenticado cambiar su contraseña actual |

---

## Endpoints expuestos

Base path: `/api/v1/auth`

### `POST /init-verification`

Llamado internamente por el User Service tras crear el usuario.

**Request body**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "hashedPassword": "$2a$10$..."
}
```

**Respuestas**

| Código | Descripción |
|---|---|
| `201` | OTP generado y enviado al correo |
| `400` | Campos faltantes o inválidos |

**Response body (201)**

```json
{ "message": "OTP sent to email" }
```

---

### `POST /verify-otp`

**Request body**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "123456"
}
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | OTP válido — cuenta activada, tokens devueltos |
| `422` | OTP inválido, expirado o ya usado |
| `429` | Máximo de intentos alcanzado — solicitar nuevo código con `/resend-otp` |

**Response body (200)**

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ..."
}
```

---

### `POST /resend-otp`

**Request body**

```json
{ "email": "usuario@escuelaing.edu.co" }
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Nuevo OTP enviado al correo |
| `422` | No se encontró cuenta para el email proporcionado |

---

### `POST /login`

**Request body**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "password": "MiContrasena123!"
}
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Login exitoso — tokens devueltos |
| `401` | Email o contraseña inválidos |
| `403` | Cuenta existe pero el correo no ha sido verificado |
| `422` | Cuenta bloqueada temporalmente por intentos fallidos |

**Response body (200)**

```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ..."
}
```

---

### `POST /refresh`

**Request body**

```json
{ "refreshToken": "eyJ..." }
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Nuevos tokens de acceso y refresh emitidos |
| `401` | Refresh token inválido o expirado |

---

### `POST /logout`

**Headers**

```
Authorization: Bearer <accessToken>
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Sesión cerrada — refresh token eliminado |
| `401` | Bearer token ausente o inválido |

---

### `POST /forgot-password`

**Request body**

```json
{ "email": "usuario@escuelaing.edu.co" }
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Código de recuperación enviado al correo |
| `422` | No se encontró cuenta para el email proporcionado |

---

### `POST /reset-password`

**Request body**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "otp": "654321",
  "newPassword": "NuevaContrasena456!"
}
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Contraseña actualizada exitosamente |
| `422` | Código de recuperación inválido, ya usado o expirado |

---

### `POST /change-password`

**Headers**

```
Authorization: Bearer <accessToken>
```

**Request body**

```json
{
  "currentPassword": "ContraActual123!",
  "newPassword": "ContraNueva456!"
}
```

**Respuestas**

| Código | Descripción |
|---|---|
| `200` | Contraseña cambiada exitosamente |
| `401` | Contraseña actual incorrecta o token inválido |

---

## Colas de mensajería — RabbitMQ

**Exchange**: `auth.exchange` (tipo: `Topic`, durable: `true`)

### Routing key `auth.otp.verification`

Publicado al enviar un OTP de registro o al reenviar OTP.
Consumido por el servicio de notificaciones para enviar el correo.

**Payload**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "otpCode": "123456"
}
```

---

### Routing key `auth.password.reset`

Publicado al solicitar recuperación de contraseña (`/forgot-password`).
Consumido por el servicio de notificaciones para enviar el código de recuperación.

**Payload**

```json
{
  "email": "usuario@escuelaing.edu.co",
  "resetCode": "654321",
  "userId": null
}
```

---

## Evidencia de pruebas unitarias

![alt text](src/main/resources/unitarias.png)

Las pruebas cubren:

- Value objects: `EmailTest`, `PasswordTest`, `OtpCodeTest`, `JwtTokenTest`, `OtpEmbeddedTest`
- Modelos de dominio: `UserTest`, `RefreshTokenTest`
- Casos de uso: `LoginUseCaseTest`, `ValidateOtpUseCaseTest`, `InitVerificationUseCaseTest`, `LogoutUseCaseTest`, `RefreshTokenUseCaseTest`, `ForgotPasswordUseCaseTest`, `ResetPasswordUseCaseTest`, `ResendOtpUseCaseTest`, `ChangePasswordUseCaseTest`
- Infraestructura: `JwtServiceTest`
- Entrypoints: `AuthControllerTest`, `GlobalExceptionHandlerTest`

---

## Evidencia del análisis de cobertura

![alt text](src/main/resources/jacoco.png)

La cobertura mínima requerida es **80% de instrucciones** (verificado por JaCoCo en la fase `verify`).
El reporte HTML se genera en `target/site/jacoco/index.html` y el XML en `target/site/jacoco/jacoco.xml` (consumido por SonarQube).

---

## Cómo ejecutar el proyecto

### Prerrequisitos

- Java 21
- Maven 3.9+
- Docker (para Redis y RabbitMQ locales)
- Variables de entorno configuradas (ver tabla abajo)

### Variables de entorno

| Variable | Descripción | Valor por defecto (dev) |
|---|---|---|
| `JWT_SECRET` | Clave secreta para firmar JWTs (mín. 32 caracteres) | `dev-secret-key-must-be-at-least-32-characters` |
| `JWT_EXPIRATION` | Expiración del access token en ms | `900000` (15 min) |
| `REDIS_HOST` | Host de Redis | `localhost` |
| `REDIS_PORT` | Puerto de Redis | `6379` |
| `REDIS_PASSWORD` | Contraseña de Redis | _(vacío)_ |
| `RABBITMQ_HOST` | Host de RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |
| `RABBITMQ_USERNAME` | Usuario de RabbitMQ | `guest` |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ | `guest` |
| `USER_SERVICE_URL` | URL base del User Service | `http://localhost:8081` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` |

### Ejecución local

```bash
# 1. Levantar Redis y RabbitMQ
docker run -d -p 6379:6379 redis:7-alpine
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 2. Clonar el repositorio
git clone <url-del-repositorio>
cd snorlax-energy-auth-service

# 3. Compilar y ejecutar pruebas
mvn clean verify

# 4. Iniciar la aplicación
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.
Swagger UI disponible en la raíz `http://localhost:8080`.

### Ejecución con Docker

```bash
docker build -t auth-service .
docker run -p 8080:8080 \
  -e JWT_SECRET=tu-secreto-de-al-menos-32-caracteres \
  -e REDIS_HOST=host.docker.internal \
  -e RABBITMQ_HOST=host.docker.internal \
  -e USER_SERVICE_URL=http://host.docker.internal:8081 \
  auth-service
```

---

## Evidencia del despliegue CI/CD

![alt text](src/main/resources/despliegue.png)

![alt text](<src/main/resources/evidencia despligue.png>)

---

## Link expuesto en Azure con Swagger



---

## Código de la implementación

El código fuente está organizado en la estructura hexagonal descrita en la sección [Cómo funciona el módulo](#cómo-funciona-el-módulo).

| Ruta | Descripción |
|---|---|
| `src/main/java/.../domain/model/` | Entidades `User` y `RefreshToken` |
| `src/main/java/.../domain/valueobjects/` | `Email`, `Password`, `JwtToken`, `OtpCode`, `OtpEmbedded` |
| `src/main/java/.../domain/ports/` | Interfaces de puertos de entrada y salida |
| `src/main/java/.../domain/exceptions/` | Excepciones de negocio tipadas |
| `src/main/java/.../application/usecase/` | Casos de uso (`LoginUseCase`, `ValidateOtpUseCase`, etc.) |
| `src/main/java/.../infrastructure/adapters/cache/` | Entidades Redis y repositorios |
| `src/main/java/.../infrastructure/external/` | `JwtService`, `EmailSenderAdapter`, cliente HTTP al User Service |
| `src/main/java/.../entrypoints/rest/controller/` | `AuthController` — expone los 9 endpoints REST |
| `src/main/resources/application-dev.yml` | Configuración del perfil de desarrollo |
| `.github/workflows/ci-cd.yml` | Pipeline CI/CD completo |

---

## Conexiones con servicios externos

| Servicio | Protocolo | Configuración | Propósito |
|---|---|---|---|
| **User Service** | HTTP REST (`RestClient`) | `USER_SERVICE_URL` | Consulta datos de usuario; actualiza estado de verificación y contraseña |
| **Redis** | TCP | `REDIS_HOST:REDIS_PORT` | Almacena OTPs, códigos de recuperación, refresh tokens y lockouts |
| **RabbitMQ** | AMQP | `RABBITMQ_HOST:RABBITMQ_PORT` | Publica eventos de email (OTP y reset de contraseña) |

---

## Pipeline de desarrollo (CI)

Ejecutado en push y pull requests hacia `develop` y `main`.

```
Push / PR
    │
    ▼
[build-test-analyze]
  ├── Checkout (fetch-depth: 0)
  ├── Setup JDK 21 + caché Maven
  ├── Levantar servicio Redis (container)
  ├── mvn clean verify sonar:sonar
  │     ├── Compilación
  │     ├── Tests unitarios (Surefire)
  │     ├── JaCoCo: cobertura ≥ 80%
  │     └── Análisis SonarCloud
  ├── Upload artefacto: JaCoCo HTML/XML
  ├── Upload artefacto: Surefire XMLs
  └── Upload artefacto: JAR ejecutable
```

---

## Pipeline de PROD (CD)

Ejecutado solo en push directo a `main`.

```
[build-test-analyze]
         │
         ▼
[docker-build-push]
  ├── Azure Login (Service Principal)
  ├── az acr login → patriciaacrprod.azurecr.io
  ├── Build imagen Docker (multi-stage)
  └── Push a ACR (tags: latest, sha-<commit>, main)
         │
         ▼
[deploy-to-azure]
  ├── Azure Login
  ├── azure/webapps-deploy
  │     └── actualiza imagen en app-patricia-auth
  └── Imprime URL: app-patricia-auth.azurewebsites.net
```

**Secrets requeridos en GitHub**

| Secret | Descripción |
|---|---|
| `AZURE_CREDENTIALS` | JSON del Service Principal (clientId, clientSecret, tenantId, subscriptionId) |
| `AZURE_REGISTRY_NAME` | Nombre del Azure Container Registry (ej. `patriciaacrprod`) |
| `AZURE_RESOURCE_GROUP` | Nombre del Resource Group (ej. `rg-patricia-prod`) |
| `AZURE_APP_SERVICE_NAME` | Nombre del App Service (ej. `app-patricia-auth`) |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |
| `SONAR_PROJECT_KEY` | Project key de SonarCloud |
| `SONAR_ORGANIZATION` | Organización en SonarCloud |
| `JWT_SECRET_TEST` | Clave JWT para el entorno de CI |

Ver la guía completa de configuración en [DEPLOYMENT_AZURE.md](DEPLOYMENT_AZURE.md).
