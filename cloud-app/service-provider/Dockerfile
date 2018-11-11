FROM maven:3.5.4-jdk-8
VOLUME /tmp
ADD target/service-provider-0.0.1-SNAPSHOT.jar cloud-app/service-provider.jar
ENTRYPOINT ["java","-Djava.securityegd=file:/dev/./urandom","-jar","/cloud-app/service-provider.jar","--spring.profiles.active=docker"]
EXPOSE 8000