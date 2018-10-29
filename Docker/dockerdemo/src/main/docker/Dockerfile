FROM openjdk:8-jdk-alpine

VOLUME /opt/common

ADD dockerdemo-1.0.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8080