FROM openjdk:17

WORKDIR /app

COPY ./build/libs/core-module-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "exec java -jar app.jar"]  
