docker run --rm -v %cd%:/app -w /app maven:3.9.6-eclipse-temurin-17 mvn -q -DskipITs clean verify# Taller de Automatización – Entrega completa

Este repositorio contiene:
- **users-api**: microservicio de ejemplo (Spring Boot) para gestión de usuarios.
- **tests-automation**: proyecto de automatización con **Cucumber + RestAssured + Faker + JSON-Schema** y reporte HTML.
- **postman/Users-API.postman_collection.json**: colección con variables (`baseUrl`, `name`, `email`), al menos dos pruebas y aserciones.
- **docker-compose.yml**: instala **Jenkins** y **SonarQube**.
- **Jenkinsfile**: pipeline que clona, compila, ejecuta pruebas (unitarias y aceptación), corre Sonar y publica reportes.

## Cómo compilar y ejecutar pruebas localmente
Requisitos: JDK 17 + Maven 3.9+  
```bash
mvn -q -DskipITs clean verify
```
El reporte de Cucumber queda en `tests-automation/target/cucumber-html-reports/overview-features.html`.

## Levantar Jenkins y SonarQube
```bash
docker compose up -d
# Jenkins en http://localhost:8080  | SonarQube en http://localhost:9000
```

## Ejecutar colección Postman
- Importa `postman/Users-API.postman_collection.json`
- Ajusta la variable `baseUrl` (por defecto `http://localhost:8080`).
- Levanta el API: `mvn -q -pl users-api spring-boot:run`
- Ejecuta toda la colección: no requiere intervención manual gracias a variables.

## Notas de cumplimiento
- **Reto 1**: Gherkin en `tests-automation/src/test/resources/features/users.feature`. Suite Postman con variables.
- **Reto 2**: Cucumber + RestAssured; validación **JSON-Schema**; automatización de pasos implementada.
- **Reto 3**: **Faker** para datos aleatorios; generación de **reporte HTML** con `maven-cucumber-reporting`; código versionable (incluye `.gitignore`).
- **Reto 4**: `docker-compose.yml` con **Jenkins** y **SonarQube**.
- **Reto 5**: `Jenkinsfile` integrando clonación, pruebas, calidad y publicación de reportes.

> Para Bruno, puedes importar la colección Postman (Bruno permite importar `.postman_collection.json`) y ejecutar con variables equivalentes.
