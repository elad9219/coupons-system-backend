# Base image for Java 11 (Fixed link)
FROM amazoncorretto:11-alpine-jdk

# The port our app runs on
EXPOSE 8080

# Copy the JAR from local target to the container
COPY target/advanced_spring-0.0.1-SNAPSHOT.jar /usr/src/app.jar

# Run the application
CMD ["java", "-jar", "/usr/src/app.jar"]