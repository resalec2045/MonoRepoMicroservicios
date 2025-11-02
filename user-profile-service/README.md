# User Profile Service (Go) — MySQL + Docker Compose

Microservicio de perfiles con persistencia **MySQL** y **Docker Compose**.
Queda listo para levantar todo con **un solo comando**:

```bash
docker compose up --build
```

> El servicio expondrá `http://localhost:8080` y MySQL quedará en `localhost:3306` (internamente como `db:3306`).

## Endpoints
- `GET /v1/profiles/me`
- `PUT /v1/profiles/me`
- `GET /v1/profiles/{userId}`
- Salud: `/health`, `/health/live`, `/health/ready`

## Autenticación de demo
`Authorization: Bearer <userId>`

## Variables de entorno (se definen en docker-compose.yml)
- `PORT=8080`
- `MYSQL_HOST=db`
- `MYSQL_PORT=3306`
- `MYSQL_DB=profiles_db`
- `MYSQL_USER=profile_user`
- `MYSQL_PASSWORD=profile_pass`

## Probar cURL (ejemplos)
```bash
# Crea/obtiene tu perfil (userId = user-123)
curl -H "Authorization: Bearer user-123" http://localhost:8080/v1/profiles/me

# Actualiza tu perfil
curl -X PUT -H "Authorization: Bearer user-123" -H "Content-Type: application/json"   -d '{
    "nickname":"Andres",
    "personal_url":"https://andres.dev",
    "contact_public": true,
    "mailing_address":"Calle 123",
    "bio":"Software engineer",
    "organization":"Universidad del Quindio",
    "country":"CO",
    "socials":{"github":"https://github.com/andres","linkedin":"https://linkedin.com/in/andres"}
  }'   http://localhost:8080/v1/profiles/me

# Ver perfil de tercero (respeta privacidad)
curl http://localhost:8080/v1/profiles/user-123
```

## Docker
- `docker compose up --build` — levanta MySQL y la app (la app espera a que MySQL esté healthy).
- `docker compose down -v` — detiene y borra volúmenes.

## Notas
- El micro realiza migración automática (crea tabla `profiles` si no existe).
- `socials` se guarda como JSON nativo en MySQL 8.
