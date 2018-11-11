FROM maven:3.5.4-jdk-8
VOLUME /tmp
ADD target/cloud-zuul-0.0.1-SNAPSHOT.jar cloud-app/cloud-zuul.jar
ENTRYPOINT ["java","-Djava.securityegd=file:/dev/./urandom","-jar","/cloud-app/cloud-zuul.jar","--spring.profiles.active=docker"]
EXPOSE 8000