package com.example.iot_management_device.config

import org.mapstruct.InjectionStrategy
import org.mapstruct.NullValueCheckStrategy

@org.mapstruct.MapperConfig(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
class MapperConfig {
}