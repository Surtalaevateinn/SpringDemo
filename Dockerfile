# First Stage: Compilation Stage (Using a Maven Image)

FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# First copy pom.xml to speed up dependency downloads using Docker caching

COPY pom.xml .

RUN mvn dependency:go-offline

# Copy source code and package it (skipping unit tests for speed)

COPY src ./src

RUN mvn clean package -DskipTests

# Second Stage: Runtime Stage (Using your previously successful lightweight runtime)

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the generated jar package from the "Compilation Stage" to the current image

# Note: The generated filename will be automatically recognized here

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]