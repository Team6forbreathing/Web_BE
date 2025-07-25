#!/bin/bash

# 이미지 빌드
docker build -t apnea-guard:latest .

# 이전 컨테이너 삭제 (존재하지 않을 경우 에러 무시)
docker rm -f apnea-guard-server 2>/dev/null || true

# 새 컨테이너 실행
docker run -d --name apnea-guard-server --env-file .env -p 88:88 apnea-guard:latest
