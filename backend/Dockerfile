# syntax=docker/dockerfile:1.7

FROM gradle:8.10.2-jdk17 AS build
WORKDIR /workspace
COPY settings.gradle build.gradle gradle.properties ./
RUN --mount=type=cache,id=gis-backend-gradle-home,target=/home/gradle/.gradle,uid=1000,gid=1000 \
    --mount=type=cache,id=gis-backend-gradle-project,target=/workspace/.gradle,uid=1000,gid=1000 \
    gradle --no-daemon dependencies >/dev/null
COPY src ./src
RUN --mount=type=cache,id=gis-backend-gradle-home,target=/home/gradle/.gradle,uid=1000,gid=1000 \
    --mount=type=cache,id=gis-backend-gradle-project,target=/workspace/.gradle,uid=1000,gid=1000 \
    gradle --no-daemon bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
