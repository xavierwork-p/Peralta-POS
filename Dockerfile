FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY services/api/pom.xml services/api/pom.xml
COPY services/api/src services/api/src

RUN mvn -f services/api/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/services/api/target/peralta-pos-api-0.1.0.jar /app/peralta-pos-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/peralta-pos-api.jar"]
