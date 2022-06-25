FROM debian:buster
LABEL maintainer="smago"

RUN apt-get update && apt-get -y upgrade
RUN apt-get -y install default-jre

COPY artifact/rest_app-1.0.jar /home

CMD ["java", "-jar", "/home/rest_app-1.0.jar"]