plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "iot_management_device"
include("internal-api", "gateway", "core", "iot-management-device")
