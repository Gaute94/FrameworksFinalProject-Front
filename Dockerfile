FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 9005
ARG JAR_FILE=redditclone-front-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} redditclone-front-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/redditclone-front-0.0.1-SNAPSHOT.jar"]