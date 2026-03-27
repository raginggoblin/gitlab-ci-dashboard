FROM maven:3.9.11-eclipse-temurin-24 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn -q -DskipTests package \
    && JAR_PATH="$(find target -maxdepth 1 -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && test -n "$JAR_PATH" \
    && cp "$JAR_PATH" /app/app.jar

FROM eclipse-temurin:24.0.2_12-jre-ubi10-minimal
WORKDIR /app

ENV TZ=Europe/Amsterdam
ENV SERVER_PORT=8080

COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
