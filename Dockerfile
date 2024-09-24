# 2. Package stage
FROM --platform=linux/arm64 openjdk:17-jdk-slim
WORKDIR /app

# 빌드 결과물을 복사
COPY ./build/libs/*.jar app.jar

EXPOSE 8080

# 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["-Dspring.profiles.active=dev"]