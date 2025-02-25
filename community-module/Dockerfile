FROM openjdk:17
WORKDIR /app
COPY ./build/libs/community-module-latest.jar app.jar
ENTRYPOINT ["sh", "-c", "exec java -jar app.jar"]
