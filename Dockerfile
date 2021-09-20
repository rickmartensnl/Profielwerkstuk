FROM maven:3.6.0-jdk-11-slim AS build
COPY src ./src
COPY pom.xml ./
RUN mvn -f pom.xml clean package

FROM openjdk:8-jre-alpine

WORKDIR /app
COPY target/Profielwerkstuk.jar ./
EXPOSE 8080

ENTRYPOINT java $SYS_PROPS -jar Profielwerkstuk.jar