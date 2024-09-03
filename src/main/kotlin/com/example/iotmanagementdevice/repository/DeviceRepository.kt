package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.Device
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository : JpaRepository<Device, Long>
