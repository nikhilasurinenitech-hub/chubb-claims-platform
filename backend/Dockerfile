FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE=claims-service
WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -pl ${SERVICE} -am -DskipTests package
FROM eclipse-temurin:21-jre
ARG SERVICE=claims-service
WORKDIR /app
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-1.0.0.jar /app/app.jar
COPY --from=build /workspace/platform-common/target/classes/com/example/platform/Healthcheck.class /app/health/com/example/platform/Healthcheck.class
USER 10001:10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java","-jar","/app/app.jar"]
