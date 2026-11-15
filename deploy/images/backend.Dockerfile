FROM eclipse-temurin:21-jre
WORKDIR /app
COPY app.jar /app/app.jar
COPY Healthcheck.class /app/health/com/example/platform/Healthcheck.class
USER 10001:10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["java","-jar","/app/app.jar"]
