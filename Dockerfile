FROM amazoncorretto:17 AS builder
WORKDIR /workspace/app

FROM builder AS iot-management-device
COPY iot-management-device/build/libs/*.jar ./iot-management-device.jar
EXPOSE 8081
CMD ["java", "-jar", "./iot-management-device.jar"]

FROM builder AS gateway
COPY gateway/build/libs/*.jar ./gateway.jar
EXPOSE 8080
CMD ["java", "-jar", "./gateway.jar"]
