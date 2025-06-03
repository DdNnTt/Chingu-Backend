#!/bin/bash

echo "[INFO] 기존 실행 중인 애플리케이션 종료 시도..."
pkill -f 'java -jar' || echo "[INFO] 종료할 프로세스가 없습니다."

echo "[INFO] 새 애플리케이션 실행 시작..."
nohup java -jar ~/build/libs/Chingu-Backend-0.0.1-SNAPSHOT.jar > ~/app.log 2>&1 &

echo "[INFO] 애플리케이션이 백그라운드에서 실행되었습니다."
