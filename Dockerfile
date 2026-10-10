
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY target/*.jar app.jar

# Run as a non-root user
RUN useradd --system --uid 10001 appuser

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
