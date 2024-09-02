package com.example.iotManagementDevice.repository

import com.example.iotManagementDevice.model.Device
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository : JpaRepository<Device, Long>
