FROM amazoncorretto:21

COPY target/*.jar /app/user.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "user.jar"]
