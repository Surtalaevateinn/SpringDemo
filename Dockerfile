# Use a lightweight JDK 17 runtime environment
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container [cite: 11]
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Startup command
ENTRYPOINT ["java", "-jar", "app.jar"]