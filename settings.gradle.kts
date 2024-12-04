plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "iot_management_device"
include(
    "common-proto",
    "core",
    "gateway",
    "grpc-api",
    "internal-api",
    "iot-management-device",
    "iot-management-device:device",
    "iot-management-device:migration",
    "iot-management-device:role",
    "iot-management-device:user",
)
