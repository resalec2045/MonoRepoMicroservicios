# Etapa build
FROM golang:1.22 AS builder
WORKDIR /app
COPY . .
# Genera go.sum dentro del contenedor
RUN go mod tidy
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o /profile-service ./cmd/server

# Etapa runtime
FROM gcr.io/distroless/base-debian12
WORKDIR /
COPY --from=builder /profile-service /profile-service
ENV PORT=8084
ENV MYSQL_HOST=db
ENV MYSQL_PORT=3306
ENV MYSQL_DB=profiles_db
ENV MYSQL_USER=profile_user
ENV MYSQL_PASSWORD=profile_pass
EXPOSE 8084
ENTRYPOINT ["/profile-service"]
