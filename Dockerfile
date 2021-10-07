FROM node:14 AS prebuild
COPY frontend /home/app/frontend
RUN cd /home/app/frontend
RUN npm i && npm run build
RUN cd /../../../

FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:8

WORKDIR /app
COPY --from=build /home/app/src/main/resources/public.pem /app/src/main/resources/public.pem
COPY --from=build /home/app/src/main/resources/private.pem /app/src/main/resources/private.pem
COPY --from=build /home/app/target/Profielwerkstuk.jar /usr/local/lib/Profielwerkstuk.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/usr/local/lib/Profielwerkstuk.jar"]