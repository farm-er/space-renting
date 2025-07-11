FROM maven:3.9.10-eclipse-temurin-24 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src src

RUN mvn clean package -DskipTests

FROM openjdk:24-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

