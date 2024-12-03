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
    "iot-management-device"
)
include("iot-management-device:device")
include("iot-management-device:user")
include("iot-management-device:role")
include("iot-management-device:migration")
