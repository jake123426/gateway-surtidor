#FROM openjdk:8
#LABEL authors="gateway-surtidor"
FROM eclipse-temurin:21-alpine
ARG JAR_FILE=target/*.jar
VOLUME /tmp
#COPY target/awesome-app-api-gateway-*.jar app.jar
COPY ${JAR_FILE} service.jar
ENV INTERNAL_LB_URI=http://localhost:8090
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/service.jar"]