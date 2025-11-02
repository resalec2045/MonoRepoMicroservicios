#!/bin/bash
# Script para inicializar SonarQube con proyecto y token automáticamente
# Requiere que SonarQube esté levantado y accesible en http://localhost:9000

SONAR_HOST="http://sonarqube:9000"
SONAR_USER="admin"
SONAR_PASS="admin"
SONAR_PROJECT_KEY="users-api"
SONAR_PROJECT_NAME="users-api"
SONAR_TOKEN_NAME="jenkins-token"
TOKEN_FILE="/sonar-init/sonar-token.txt"

# Esperar a que SonarQube esté disponible
until $(curl --output /dev/null --silent --head --fail "$SONAR_HOST/api/system/health"); do
    echo "Esperando a que SonarQube esté disponible..."
    sleep 5
done

# Crear token de análisis
TOKEN=$(curl -u $SONAR_USER:$SONAR_PASS -X POST "$SONAR_HOST/api/user_tokens/generate" -d "name=$SONAR_TOKEN_NAME" | grep -o '"token":"[^"]*"' | cut -d':' -f2 | tr -d '"')

# Crear proyecto si no existe
curl -u $SONAR_USER:$SONAR_PASS -X POST "$SONAR_HOST/api/projects/create" -d "name=$SONAR_PROJECT_NAME" -d "project=$SONAR_PROJECT_KEY"

# Guardar token en archivo compartido
mkdir -p /sonar-init
echo $TOKEN > $TOKEN_FILE
chmod 600 $TOKEN_FILE

echo "Token SonarQube generado y guardado en $TOKEN_FILE"
