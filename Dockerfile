# Use a base image with JDK 17 or 11 (specify platform for ARM compatibility)
FROM --platform=linux/amd64 openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the packaged application JAR file into the container
COPY target/foodordering-0.0.1-SNAPSHOT.jar /app/food-ordering.jar

# Expose port 8080 (default port for Spring Boot applications)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "food-ordering.jar"]