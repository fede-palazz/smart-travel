FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy only the correct Spring Boot fat jar (exclude *.original)
COPY target/bff-api-*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
