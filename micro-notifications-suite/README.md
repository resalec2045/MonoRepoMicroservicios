# Micro Notifications Suite

Cumple con los 5 retos del taller de comunicaciones.

## Requisitos
- Docker y Docker Compose
- JDK 21 (para compilar) — o use solo Docker para ejecutar
- Bash o CMD para usar `mvnw`

## Cómo compilar
```bash
./mvnw -q -DskipTests package
```

## Cómo ejecutar todo
```bash
docker compose up --build
```
- RabbitMQ UI: http://localhost:15672 (guest/guest)
- Auth: http://localhost:8081
- Notifications: http://localhost:8082
- Orchestrator: http://localhost:8083

## Flujo
1. Regístrese en auth (`POST /api/auth/register`). Esto emite evento `user.registered`.
2. Orchestrator escucha eventos y envía solicitud asíncrona de notificación al `notification-service` vía RabbitMQ.
3. `notification-service` elige proveedores por canal (email/sms/whatsapp), simula envíos y persiste estados.
4. Consulte canales, estados, paginación y programe envíos futuros.

## JWT
- Obtenga token con `POST /api/auth/login`. 
- Use en llamadas protegidas al `notification-service` como `Authorization: Bearer <token>`.

## Programación de notificaciones
- `POST /api/notifications/schedule` con `sendAt` ISO-8601 futuro. Un scheduler interno encola la entrega cuando corresponda.

## Datos de prueba
- El `auth-service` usa Postgres; `notification-service` su propia base embebida (H2) en local y Postgres en docker.
- Orchestrator no requiere base.

## Eventos
- `user.registered`
- `user.loggedin`
- `user.password.reset.requested`
- `user.password.updated`
- Solicitudes de envío: `notify.request`

## Endpoints principales
- **notification-service**
  - `GET /api/channels`
  - `POST /api/notifications` (inmediato)
  - `POST /api/notifications/schedule` (programado)
  - `GET /api/notifications/{id}` (status)
  - `GET /api/notifications` (filtros/paginación)
