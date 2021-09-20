FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:8-jre-alpine

WORKDIR /app
COPY target/Profielwerkstuk.jar ./
EXPOSE 8080

ENTRYPOINT java $SYS_PROPS -jar Profielwerkstuk.jar