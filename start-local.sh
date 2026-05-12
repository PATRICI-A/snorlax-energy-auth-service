#!/bin/bash
# Carga las variables del .env y levanta el auth-service
set -a
source "$(dirname "$0")/.env"
set +a

echo ""
echo "=== Auth Service ==="
echo "Puerto: $PORT"
echo "Redis:  $REDIS_HOST:$REDIS_PORT"
echo "User Service: $USER_SERVICE_URL"
echo ""

cd "$(dirname "$0")"
./mvnw spring-boot:run -q
