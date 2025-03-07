FROM maven:3.9.4-eclipse-temurin-21 AS build
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]