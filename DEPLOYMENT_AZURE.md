# Guía de Despliegue — Azure App Service

**PATRICI.A** · Estándar de Despliegue para todos los Microservicios

Esta guía aplica para **cualquier microservicio** del proyecto. Reemplaza los valores entre `< >` con los datos de tu servicio. Todo se hace desde el portal web de Azure ([portal.azure.com](https://portal.azure.com)).

---

## Resumen de la arquitectura

```
GitHub Actions
      │
      ├─ Job 1: Build + Tests + SonarCloud
      └─ Job 2: Deploy JAR → Azure App Service
                              │
                              └─ Kong API Gateway (registro manual / inicial)
```

**Recursos que se crean en Azure:**

| Recurso | Nombre sugerido | Nota |
|---|---|---|
| Resource Group | `rg-patricia-prod` | Uno para todo el proyecto |
| App Service Plan | `asp-patricia-prod` | Uno para todo el proyecto |
| App Service | `app-patricia-<nombre-servicio>` | Uno por microservicio |
| Service Principal | `sp-patricia-github-actions` | Uno para todo el proyecto |

---

## Paso 1 — Crear el Resource Group

> Si ya existe `rg-patricia-prod` en el proyecto, salta este paso.

1. Entra a [portal.azure.com](https://portal.azure.com)
2. En la barra de búsqueda escribe **"Resource groups"** → clic en el resultado
3. Clic en **+ Crear**
4. Llena los campos:
   - **Suscripción**: la del proyecto
   - **Grupo de recursos**: `rg-patricia-prod`
   - **Región**: `Canada Central`
5. Clic en **Revisar y crear** → **Crear**

---

## Paso 2 — Crear el App Service Plan

> Si ya existe `asp-patricia-prod` en el proyecto, salta este paso.

1. En la barra de búsqueda escribe **"App Service plans"** → clic en el resultado
2. Clic en **+ Crear**
3. Llena los campos:
   - **Suscripción**: la del proyecto
   - **Grupo de recursos**: `rg-patricia-prod`
   - **Nombre**: `asp-patricia-prod`
   - **Sistema operativo**: `Linux`
   - **Región**: `Canada Central`
   - **Plan de tarifa**: `Basic B1`
4. Clic en **Revisar y crear** → **Crear**

---

## Paso 3 — Crear el App Service del microservicio

1. En la barra de búsqueda escribe **"App Services"** → clic en el resultado
2. Clic en **+ Crear** → **Aplicación web**
3. **Pestaña Datos básicos**:
   - **Suscripción**: la del proyecto
   - **Grupo de recursos**: `rg-patricia-prod`
   - **Nombre**: `app-patricia-<nombre-servicio>`
   - **Publicar**: `Código`
   - **Pila del entorno de tiempo de ejecución**: `Java 21`
   - **Pila de servidor web Java**: `Java SE (Embedded Web Server)`
   - **Sistema operativo**: `Linux`
   - **Región**: `Canada Central`
   - **Plan de App Service**: `asp-patricia-prod`
4. Clic en **Revisar y crear** → **Crear**

---

## Paso 4 — Configurar variables de entorno

1. Entra al App Service `app-patricia-<nombre-servicio>`
2. En el menú izquierdo → **Configuración** → **Variables de entorno**
3. Clic en **+ Agregar** para cada variable

**Variables obligatorias en todos los microservicios:**

| Nombre | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` |
| `WEBSITES_PORT` | `8080` |

**Variables según lo que use cada microservicio:**

| Nombre | Descripción |
|---|---|
| `JWT_SECRET` | Clave de firma JWT (mín. 32 caracteres) |
| `JWT_EXPIRATION` | Expiración del access token en ms (ej. `900000`) |
| `REDIS_HOST` | Host de Redis |
| `REDIS_PORT` | Puerto de Redis (default: `6379`) |
| `REDIS_PASSWORD` | Contraseña de Redis |
| `REDIS_SSL` | TLS habilitado (`true` / `false`) |
| `RABBITMQ_HOST` | Host de RabbitMQ |
| `RABBITMQ_PORT` | Puerto de RabbitMQ (default: `5672`) |
| `RABBITMQ_USERNAME` | Usuario de RabbitMQ |
| `RABBITMQ_PASSWORD` | Contraseña de RabbitMQ |
| `MONGODB_URI` | URI de conexión a MongoDB Atlas |
| `MONGODB_DATABASE` | Nombre de la base de datos |
| `<NOMBRE>_SERVICE_URL` | URL base de otro microservicio |

4. Clic en **Guardar** (arriba)

---

## Paso 5 — Crear el Service Principal para GitHub Actions

> Si ya existe `sp-patricia-github-actions` en el proyecto, pídele el JSON `AZURE_CREDENTIALS` al compañero que lo creó y salta al Paso 6.

1. En la barra de búsqueda escribe **"Microsoft Entra ID"** → clic en el resultado
2. En el menú izquierdo → **Administrar** → **Registros de aplicaciones**
3. Clic en **+ Nuevo registro**:
   - **Nombre**: `sp-patricia-github-actions`
   - Todo lo demás por defecto
   - Clic en **Registrar**
4. Anota estos dos valores de la pantalla de Overview:
   - **Id. de aplicación (cliente)** → este es el `clientId`
   - **Id. de directorio (inquilino)** → este es el `tenantId`
5. En el menú izquierdo → **Certificados y secretos** → **+ Nuevo secreto de cliente**:
   - **Descripción**: `github-actions`
   - **Expira**: `24 months`
   - Clic en **Agregar**
   - Copia el **Valor** inmediatamente — solo se muestra una vez → este es el `clientSecret`
6. Asignar el rol al App Service:
   - Busca y entra al App Service `app-patricia-<nombre-servicio>`
   - Menú izquierdo → **Control de acceso (IAM)**
   - Clic en **+ Agregar** → **Agregar asignación de roles**
   - Busca y selecciona **Colaborador** → **Siguiente**
   - Clic en **+ Seleccionar miembros** → busca `sp-patricia-github-actions` → **Seleccionar**
   - Clic en **Revisar y asignar**
7. Obtén el **Subscription ID**:
   - En la barra de búsqueda escribe **"Suscripciones"**
   - Copia el **ID de suscripción** → este es el `subscriptionId`

Con esos 4 datos construye el JSON para el secret `AZURE_CREDENTIALS`:

```json
{
  "clientId":       "<Id. de aplicación (cliente)>",
  "clientSecret":   "<Valor del secreto>",
  "tenantId":       "<Id. de directorio (inquilino)>",
  "subscriptionId": "<ID de suscripción>"
}
```

---

## Paso 6 — Configurar Secrets en GitHub

1. Ve al repositorio en GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. Clic en **New repository secret** para cada uno:

| Secret | Valor |
|---|---|
| `AZURE_CREDENTIALS` | El JSON completo del Paso 5 |
| `AZURE_APP_SERVICE_NAME` | `app-patricia-<nombre-servicio>` |
| `SONAR_TOKEN` | Token generado en SonarCloud (Paso 7) |
| `SONAR_PROJECT_KEY` | Project key de SonarCloud (Paso 7) |
| `SONAR_ORGANIZATION` | Organización de SonarCloud (Paso 7) |
| `JWT_SECRET_TEST` | Cualquier string de ≥ 32 caracteres (solo para CI) |

---

## Paso 7 — Configurar SonarCloud

1. Entra a [sonarcloud.io](https://sonarcloud.io) con tu cuenta de GitHub
2. Clic en **+** (arriba a la derecha) → **Analyze new project**
3. Selecciona tu repositorio → **Set up**
4. Elige **GitHub Actions** como método de análisis
5. Para obtener el `SONAR_TOKEN`:
   - Clic en tu avatar (arriba a la derecha) → **My Account**
   - Pestaña **Security** → escribe un nombre → **Generate**
   - Copia el token generado
6. El `SONAR_PROJECT_KEY` y `SONAR_ORGANIZATION` aparecen en la pantalla de configuración del proyecto
7. En SonarCloud → **Administration** → **Analysis Method** → desactiva **Automatic Analysis**

---

## Paso 8 — Primer despliegue

En tu terminal, desde la carpeta del proyecto:

```bash
git add .
git commit -m "chore: configure Azure App Service deployment"
git push origin main
```

Ve a GitHub → pestaña **Actions** y verás el pipeline corriendo:

```
build-test-analyze  →  deploy-to-azure
     ~3-5 min              ~1-2 min
```

Al terminar la app queda disponible en:
- **API**: `https://app-patricia-<nombre-servicio>.azurewebsites.net/api/v1/<ruta>`
- **Swagger**: `https://app-patricia-<nombre-servicio>.azurewebsites.net`

---

## Paso 9 — Registrar en Kong

Una vez desplegado, registra el microservicio en Kong para que el Gateway enrute el tráfico.

```bash
KONG_ADMIN="http://<url-de-tu-kong>:8001"
APP_URL="https://app-patricia-<nombre-servicio>.azurewebsites.net"

# Registrar el servicio
curl -X PUT $KONG_ADMIN/services/<nombre-servicio> \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"<nombre-servicio>\",\"url\":\"$APP_URL\"}"

# Crear la ruta
curl -X POST $KONG_ADMIN/services/<nombre-servicio>/routes \
  -H "Content-Type: application/json" \
  -d '{"name":"<nombre-servicio>-route","paths":["/api/v1/<ruta>"],"strip_path":false}'
```

---

## Rollback

Si un deploy falla y necesitas volver a una versión anterior:

1. Entra al App Service en el portal
2. Menú izquierdo → **Centro de implementación** → **Registros**
3. Selecciona una versión anterior → **Reimplementar**

---

## Ver logs en tiempo real

1. Entra al App Service en el portal
2. Menú izquierdo → **Flujo de registro**
3. Los logs aparecen en tiempo real

---

## Estándar de nombres

| Recurso | Convención | Ejemplo |
|---|---|---|
| Resource Group | `rg-patricia-<entorno>` | `rg-patricia-prod` |
| App Service Plan | `asp-patricia-<entorno>` | `asp-patricia-prod` |
| App Service | `app-patricia-<servicio>` | `app-patricia-auth` |
| Service Principal | `sp-patricia-github-actions` | (fijo para todo el proyecto) |

## Estándar de variables de entorno

| Categoría | Variable | Descripción |
|---|---|---|
| Servidor | `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev` / `prod`) |
| Servidor | `WEBSITES_PORT` | Puerto de la app (siempre `8080`) |
| JWT | `JWT_SECRET` | Clave de firma (mín. 32 chars) |
| JWT | `JWT_EXPIRATION` | Expiración access token en ms |
| Redis | `REDIS_HOST` | Host de Redis |
| Redis | `REDIS_PORT` | Puerto (default: `6379`) |
| Redis | `REDIS_PASSWORD` | Contraseña |
| Redis | `REDIS_SSL` | TLS habilitado (`true`/`false`) |
| RabbitMQ | `RABBITMQ_HOST` | Host del broker |
| RabbitMQ | `RABBITMQ_PORT` | Puerto (default: `5672`) |
| RabbitMQ | `RABBITMQ_USERNAME` | Usuario |
| RabbitMQ | `RABBITMQ_PASSWORD` | Contraseña |
| MongoDB | `MONGODB_URI` | URI de conexión a Atlas |
| MongoDB | `MONGODB_DATABASE` | Nombre de la base de datos |
| Servicios | `<NOMBRE>_SERVICE_URL` | URL base de otro microservicio |
