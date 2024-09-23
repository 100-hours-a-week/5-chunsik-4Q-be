# 1. Build stage
FROM gradle:8.10-jdk17 AS build
WORKDIR /app

# 애플리케이션의 소스를 컨테이너로 복사
COPY . .

ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=${SENTRY_AUTH_TOKEN}

# 빌드 수행 (Gradle Wrapper 사용 시 "./gradlew build" 명령어를 사용)
RUN gradle bootJar --no-daemon

# 2. Package stage
FROM --platform=linux/arm64 openjdk:17-jdk-slim
WORKDIR /app

# 빌드 결과물을 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["-Dspring.profiles.active=dev"]