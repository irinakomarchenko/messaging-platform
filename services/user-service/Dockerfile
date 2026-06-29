FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY common ./common
COPY services ./services

RUN mvn -pl services/user-service -am clean package -DskipTests

RUN JAR_FILE=$(find services/user-service/target -maxdepth 1 -name "*.jar" ! -name "*original*" | head -n 1) \
    && cp "$JAR_FILE" /app/app.jar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/app.jar app.jar

USER app

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]