FROM amazoncorretto:17
WORKDIR /workspace/app

COPY build/libs/*.jar ./app.jar
CMD ["java", "-jar", "./app.jar"]