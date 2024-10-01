#!/bin/zsh

aws ecr get-login-password --region ap-northeast-2 --profile default | \
 docker login --username AWS --password-stdin 202533511551.dkr.ecr.ap-northeast-2.amazonaws.com

cd ../..
docker build -t chunsik/dev/be .

docker tag chunsik/dev/be:latest 202533511551.dkr.ecr.ap-northeast-2.amazonaws.com/chunsik/dev/be:0925

docker push 202533511551.dkr.ecr.ap-northeast-2.amazonaws.com/chunsik/dev/be:0925