# 빌드
FROM gradle:8.6-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build

# 실행
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]