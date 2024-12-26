FROM ubuntu:latest
LABEL authors="Alvin"

ENTRYPOINT ["top", "-b"]

FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY . .
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
