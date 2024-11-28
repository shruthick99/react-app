# Use a lightweight Java 17 runtime
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the built WAR file into the container
COPY target/check-in-0.0.1-SNAPSHOT.war app.war

# Expose the application port
EXPOSE 8081

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.war"]

