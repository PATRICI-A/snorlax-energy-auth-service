# Guía de Despliegue — AWS Lambda

Guía paso a paso para desplegar el auth service en AWS Lambda con CD automático desde GitHub Actions.

---

## Requisitos previos

- Cuenta de AWS (o AWS Academy)
- Repositorio en GitHub con el código
- Docker instalado localmente (solo para pruebas locales)

---

## Paso 1 — Crear repositorio en Amazon ECR

ECR es el registro de imágenes Docker de AWS, equivalente a Docker Hub pero privado.

1. Entra a [console.aws.amazon.com](https://console.aws.amazon.com)
2. Buscador → **ECR** → **Elastic Container Registry**
3. Click **Create repository**
4. Nombre: `auth-service`
5. Mutabilidad: **Mutable**
6. Cifrado: **AES-256**
7. Click **Create repository**

Anota la URI del repositorio, se verá así:
```
<account-id>.dkr.ecr.us-east-1.amazonaws.com/auth-service
```

---

## Paso 2 — Crear la función Lambda

> **AWS Academy**: no puedes crear roles IAM. Usa el rol `LabRole` que ya existe en IAM → Roles.

1. Buscador → **Lambda** → **Create function**
2. Selecciona **"Imagen del contenedor"**
3. Nombre: `snorlax-auth-service`
4. En **Container image URI** → click **Examinar imágenes** → selecciona el repo `auth-service` → imagen `latest`

   > La primera vez el repositorio está vacío. Ve al **Paso 6** para subir una imagen placeholder primero, luego vuelve aquí.

5. Architecture: **x86_64**
6. Expande **Configuración adicional** → **Rol de ejecución** → **Usar un rol existente** → `LabRole`
7. Click **Crear función**

---

## Paso 3 — Configurar memoria y timeout

Spring Boot necesita más recursos que el default de Lambda.

1. Función Lambda → **Configuración** → **Configuración general** → **Editar**
2. **Memoria**: `1024 MB`
3. **Timeout**: `30 segundos`
4. Guardar

---

## Paso 4 — Configurar variables de entorno

1. Función Lambda → **Configuración** → **Variables de entorno** → **Editar**
2. Agrega las siguientes variables:

| Clave | Valor |
|-------|-------|
| `SPRING_PROFILES_ACTIVE` | `dev` |
| `PORT` | `9090` |
| `JWT_SECRET` | mínimo 32 caracteres |
| `JWT_EXPIRATION` | `900000` |
| `MAIL_HOST` | `smtp.gmail.com` |
| `MAIL_PORT` | `587` |
| `MAIL_USERNAME` | tu correo Gmail |
| `MAIL_PASSWORD` | contraseña de aplicación de Gmail* |
| `MAIL_DEV_MODE` | `false` |
| `REDIS_HOST` | host de tu Redis |
| `REDIS_PORT` | `6379` |
| `REDIS_PASSWORD` | password de tu Redis |
| `REDIS_SSL` | `true` (si el Redis usa SSL) |
| `USER_SERVICE_URL` | URL del microservicio de usuarios |

> *`MAIL_PASSWORD` debe ser una **contraseña de aplicación** de Gmail, no tu contraseña normal.
> Ve a: Cuenta Google → Seguridad → Verificación en 2 pasos → Contraseñas de aplicaciones.

### Conectar microservicios adicionales

`USER_SERVICE_URL` apunta al microservicio de usuarios, que es el único que el auth service llama actualmente. Cuando ese microservicio esté desplegado en Lambda, tendrá su propia Function URL y esa es la que va aquí.

El auth service usa esta URL para:
- Buscar usuario por email al hacer login
- Buscar usuario por ID
- Marcar usuario como verificado tras el OTP
- Actualizar contraseña en reset password

**Si en el futuro el auth service necesita llamar a más microservicios**, simplemente agrega una variable de entorno por cada uno:

| Clave | Valor |
|-------|-------|
| `USER_SERVICE_URL` | `https://xxxxxxxx.lambda-url.us-east-1.on.aws/` |
| `NOTIFICATION_SERVICE_URL` | `https://yyyyyyyy.lambda-url.us-east-1.on.aws/` |
| `PAYMENT_SERVICE_URL` | `https://zzzzzzzz.lambda-url.us-east-1.on.aws/` |

> **Importante**: solo necesitas agregar aquí los microservicios que el **auth service llama directamente**. Si son microservicios independientes entre sí, cada uno maneja sus propias variables de conexión.

---

## Paso 5 — Crear URL pública (Function URL)

1. Función Lambda → **Configuración** → **URL de la función**
2. Click **Crear URL de función**
3. Tipo de autorización: **NONE** (Spring Security maneja la autenticación)
4. Guardar

Te dará una URL pública como:
```
https://xxxxxxxx.lambda-url.us-east-1.on.aws/
```

El Swagger de la API estará en:
```
https://xxxxxxxx.lambda-url.us-east-1.on.aws/swagger-ui/index.html
```

---

## Paso 6 — Subir imagen inicial a ECR (primera vez)

La primera vez que creas la función Lambda necesitas una imagen en ECR.
Usa **CloudShell** (ícono `>_` en la consola de AWS) para subirla:

```bash
# Autenticarse en ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Clonar el repositorio y entrar a la rama correcta
git clone https://github.com/<org>/<repo>.git
cd <repo>
git checkout <rama>

# Build y push a ECR
docker build -t <account-id>.dkr.ecr.us-east-1.amazonaws.com/auth-service:latest .
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/auth-service:latest

# Actualizar Lambda con la nueva imagen
aws lambda update-function-code \
  --function-name snorlax-auth-service \
  --image-uri <account-id>.dkr.ecr.us-east-1.amazonaws.com/auth-service:latest
```

---

## Paso 7 — Configurar GitHub Secrets

Los secrets permiten que GitHub Actions se autentique en AWS para el CD automático.

Ve a tu repo en GitHub → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**.

Agrega estos secrets:

| Secret | Valor |
|--------|-------|
| `AWS_ACCESS_KEY_ID` | Tu access key de AWS |
| `AWS_SECRET_ACCESS_KEY` | Tu secret key de AWS |
| `AWS_SESSION_TOKEN` | Tu session token (solo AWS Academy) |
| `AWS_REGION` | `us-east-1` |
| `ECR_REPOSITORY` | `auth-service` |
| `LAMBDA_FUNCTION_NAME` | `snorlax-auth-service` |
| `JWT_SECRET_TEST` | Secret para los tests en CI (mín. 32 chars) |

> **AWS Academy**: las credenciales (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`) son temporales y se resetean cada sesión (~4 horas).
> Cada vez que inicies una nueva sesión de laboratorio debes actualizar estos 3 secrets con los nuevos valores que aparecen en **AWS Details → AWS CLI**.

---

## Paso 8 — Flujo de CD automático

Una vez configurado todo, el flujo automático es:

```
Push a main
    │
    ├─► CI: Build + Tests (con Redis en contenedor)
    │
    ├─► Docker: Build imagen → push a ghcr.io
    │
    └─► Deploy: Build imagen → push a ECR → actualiza Lambda
```

El deploy solo se dispara en push directo a `main`. Los Pull Requests solo corren el CI.

---

## Redis — Opciones de configuración

El auth service usa Redis para almacenar:
- Refresh tokens (expiran en 7 días)
- Códigos OTP (expiran en 10 minutos)
- Bloqueos de cuenta por intentos fallidos (expiran en 30 minutos)

### Opción A — Redis en Docker (local)

Para desarrollo local agrega Redis al `docker-compose.yml`:

```yaml
redis:
  image: redis:7-alpine
  container_name: patricia-redis
  ports:
    - "6379:6379"
  restart: unless-stopped
```

Variables de entorno locales:
```
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL=false
```

### Opción B — Upstash (producción, gratis)

Upstash es un servicio de Redis en la nube con capa gratuita. Ideal para producción sin infraestructura propia.

1. Ve a [upstash.com](https://upstash.com) → **Sign up**
2. Click **Create database**
3. Nombre: `snorlax-redis`, región: `us-east-1`
4. Click **Create**
5. En el dashboard verás:
   - **Endpoint** → `REDIS_HOST`
   - **Port** → `REDIS_PORT`
   - **Password** → `REDIS_PASSWORD`

Variables de entorno para Lambda con Upstash:
```
REDIS_HOST=<endpoint>.upstash.io
REDIS_PORT=6379
REDIS_PASSWORD=<password>
REDIS_SSL=true
```

> Upstash usa SSL/TLS (`rediss://`), por eso `REDIS_SSL=true` es obligatorio.

---

## Verificar el despliegue

Después de cada deploy puedes verificar el estado desde CloudShell:

```bash
aws lambda get-function \
  --function-name snorlax-auth-service \
  --query 'Configuration.[FunctionName,LastModified,State]' \
  --output table
```

O simplemente abre el Swagger en el navegador:
```
https://<function-url>.lambda-url.us-east-1.on.aws/swagger-ui/index.html
```
