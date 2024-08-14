package com.example.iot_management_device.repository

import com.example.iot_management_device.model.Device
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository : JpaRepository<Device, Long> {
}