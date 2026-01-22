FROM openjdk:11-slim


COPY target/spring_coupons_project-*.jar /usr/src/spring_coupons_project.jar

COPY src/main/resources/application.properties /opt/conf/application.properties

EXPOSE 8080

CMD ["java", "-jar", "/usr/src/spring_coupons_project.jar", "--spring.config.location=file:/opt/conf/application.properties"]