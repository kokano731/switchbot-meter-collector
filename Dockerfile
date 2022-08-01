FROM openjdk:17-slim

WORKDIR /app
COPY . .
RUN ["./gradlew", "build"]
ENTRYPOINT [ "java", "-jar",  "build/libs/switchbot-meter-collector-0.0.1-SNAPSHOT.jar"]