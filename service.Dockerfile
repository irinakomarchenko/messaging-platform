# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

ARG SERVICE_PATH

COPY pom.xml .

COPY services/user-service/pom.xml ./services/user-service/
COPY services/group-channel-service/pom.xml ./services/group-channel-service/
# COPY services/chat-history-service/pom.xml ./services/chat-history-service/
# COPY services/connection-management-service/pom.xml ./services/connection-management-service/
# COPY services/messaging-service/pom.xml ./services/messaging-service/
# COPY services/api-gateway-service/pom.xml ./services/api-gateway-service/


RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${SERVICE_PATH} -am dependency:go-offline -DskipTests

COPY services ./services

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${SERVICE_PATH} -am clean package -DskipTests

RUN JAR_FILE=$(find ${SERVICE_PATH}/target -maxdepth 1 -name "*.jar" ! -name "*original*" | head -n 1) \
    && cp "$JAR_FILE" /app/app.jar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

ARG SERVICE_PORT

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/app.jar app.jar

USER app

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]