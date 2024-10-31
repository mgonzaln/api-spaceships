FROM openjdk:21-jdk-slim AS build
LABEL authors="mauriciogonzalez"
VOLUME /tmp
ARG JAR_FILE=target/spaceship-api.jar
ADD ${JAR_FILE} spaceship-api.jar
EXPOSE 8888
ENTRYPOINT ["java","-jar","/spaceship-api.jar"]