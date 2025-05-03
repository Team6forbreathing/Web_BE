# OpenJDK 17 기반 Alpine 이미지 사용
FROM openjdk:17-alpine

# 필수 유틸리티 설치 (git, findutils)
RUN apk update && apk add --no-cache git findutils

# 컨테이너 작업 디렉토리 설정
WORKDIR /app

# 로컬 파일 복사
COPY . .

# 환경 변수 설정 (빌드 시 필요)
ARG SECRET_KEY
ARG ACCESS_TOKEN_EXPIRY
ARG REFRESH_TOKEN_EXPIRY
ARG MYSQL_URL
ARG MYSQL_USER
ARG MYSQL_PW
ARG INFLUX_URL
ARG INFLUX_ORG
ARG INFLUX_BUCKET
ARG INFLUX_TOKEN

# ARG 값을 ENV로 변환 (RUN에서 정상적으로 사용되도록)
ENV SECRET_KEY=${SECRET_KEY}
ENV ACCESS_TOKEN_EXPIRY=${ACCESS_TOKEN_EXPIRY}
ENV REFRESH_TOKEN_EXPIRY=${REFRESH_TOKEN_EXPIRY}
ENV MYSQL_URL=${MYSQL_URL}
ENV MYSQL_USER=${MYSQL_USER}
ENV MYSQL_PW=${MYSQL_PW}
ENV INFLUX_URL=${INFLUX_URL}
ENV INFLUX_ORG=${INFLUX_ORG}
ENV INFLUX_BUCKET=${INFLUX_BUCKET}
ENV INFLUX_TOKEN=${INFLUX_TOKEN}

# Gradle 실행 권한 부여 및 빌드
RUN chmod +x ./gradlew && ./gradlew clean build --no-daemon

# 실행 시 사용할 환경 변수 설정
EXPOSE 8080

# JAR 파일 실행
CMD ["java", "-Duser.timezone=GMT+9", "-Djava.security.egd=file:/dev/./urandom", "-jar", "build/libs/sleeping-0.0.1-SNAPSHOT.jar"]
