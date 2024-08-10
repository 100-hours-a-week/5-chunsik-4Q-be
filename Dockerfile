

FROM openjdk:17-jdk-slim

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

VOLUME /tmp

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
