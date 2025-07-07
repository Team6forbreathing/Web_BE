# OpenJDK 17 기반 Alpine 이미지 사용
FROM openjdk:17-alpine

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew && ./gradlew clean build -x test --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/sleeping-0.0.1-SNAPSHOT.jar"]