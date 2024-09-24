FROM --platform=linux/arm64 openjdk:17-jdk-slim
WORKDIR /app

ARG SPRING_PROFILE=dev
ENV SPRING_PROFILE=${SPRING_PROFILE}

# 빌드 결과물을 복사
COPY ./build/libs/*.jar app.jar

# yaml
COPY ./src/main/resources ./config

# entrypoint
COPY ./entrypoint.sh ./entrypoint.sh
RUN chmod +x ./entrypoint.sh

EXPOSE 8080

# 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["./entrypoint.sh"]