#!/bin/bash

ROOT_PATH="/home/ec2-user/app"
JAR="$ROOT_PATH/app.jar"

STOP_LOG="$ROOT_PATH/stop.log"
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

SERVICE_PID=$(pgrep -f $JAR) # 실행중인 Spring 서버의 PID

if [ -z "$SERVICE_PID" ]; then
  echo -e "${RED}서비스 NotFound${NC}" >> $STOP_LOG
else
  echo -e "${GREEN}서비스 종료${NC}" >> $STOP_LOG
  kill "$SERVICE_PID"
  # kill -9 $SERVICE_PID # 강제 종료를 하고 싶다면 이 명령어 사용
fi