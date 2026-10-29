
FROM amazoncorretto:21-alpine

WORKDIR /app

EXPOSE 8080

COPY target/advanced_spring-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
