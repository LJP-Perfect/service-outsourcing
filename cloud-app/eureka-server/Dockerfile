FROM maven:3.5.4-jdk-8
VOLUME /tmp
ADD target/eureka-server-0.0.1-SNAPSHOT.jar cloud-app/eureka-server.jar
ENTRYPOINT ["java","-Djava.securityegd=file:/dev/./urandom","-jar","/cloud-app/eureka-server.jar","--spring.profiles.active=docker"]
EXPOSE 8000