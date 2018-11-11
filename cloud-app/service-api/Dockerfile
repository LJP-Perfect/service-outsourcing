FROM maven:3.5.4-jdk-8
VOLUME /tmp
ADD target/service-api-0.0.1-SNAPSHOT.jar cloud-app/service-api.jar
ENTRYPOINT ["java","-Djava.securityegd=file:/dev/./urandom","-jar","/cloud-app/service-api.jar","--spring.profiles.active=docker"]
EXPOSE 8000