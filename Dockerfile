FROM openjdk:8-jre-alpine

WORKDIR /app
COPY target/Profielwerkstuk.jar ./
EXPOSE 8080

ENTRYPOINT java $SYS_PROPS -jar Profielwerkstuk.jar