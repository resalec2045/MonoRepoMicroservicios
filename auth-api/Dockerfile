# ===== Build =====
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Construir jar ejecutable (Spring Boot repackage)
RUN mvn -q -DskipTests clean package \
 && ls -lh target \
 && JAR_FILE="$(ls target/*.jar | grep -Ev '(original|plain)\\.jar$' | head -n1)" \
 && echo "Usando JAR: ${JAR_FILE}" \
 && cp "${JAR_FILE}" /app/app.jar \
 && ls -lh /app

# ===== Runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
