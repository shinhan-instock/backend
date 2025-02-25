FROM openjdk:17

WORKDIR /app

COPY ./core-module/build/libs/core-module-latest.jar app.jar

ENTRYPOINT ["sh", "-c", "exec java -jar app.jar"]
