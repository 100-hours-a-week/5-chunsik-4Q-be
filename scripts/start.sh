#!/bin/bash

ROOT_PATH="/home/ec2-user/app"
JAR="$ROOT_PATH/app.jar"

APP_LOG="$ROOT_PATH/application.log"
ERROR_LOG="$ROOT_PATH/error.log"
START_LOG="$ROOT_PATH/start.log"

GREEN='\033[0;32m'
NC='\033[0m' # No Color

NOW=$(date +%c)

echo -e "${GREEN}[$NOW] $JAR 복사${NC}" >> $START_LOG
cp $ROOT_PATH/build/libs/app.jar $JAR

echo -e "${GREEN}[$NOW] > $JAR 실행${NC}" >> $START_LOG
nohup java -jar $JAR > $APP_LOG 2> $ERROR_LOG &

# 5초 대기
sleep 5

SERVICE_PID=$(pgrep -f $JAR)
echo -e "${GREEN}[$NOW] > 서비스 PID: $SERVICE_PID${NC}" >> $START_LOG