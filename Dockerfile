# =================== BUILD ===================
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/

RUN sed -i 's/\r$//' gradlew

RUN chmod +x gradlew

COPY src ./src

RUN ./gradlew clean build -x test --no-daemon

# =================== FINAL IMAGE ===================
FROM openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]