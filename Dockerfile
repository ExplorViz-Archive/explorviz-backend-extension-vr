FROM openjdk:11-jre-slim

RUN mkdir /explorviz
WORKDIR /explorviz
COPY build/libs/explorviz-backend-extension-vr.jar .

CMD java -jar explorviz-backend-extension-vr.jar