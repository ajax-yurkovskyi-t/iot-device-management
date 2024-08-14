package com.example.iot_management_device.service.device

import com.example.iot_management_device.model.Device
import com.example.iot_management_device.repository.DeviceRepository

class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository
): DeviceService {
    override fun create(device: Device): Device {
        return deviceRepository.save(device)
    }

    override fun getById(deviceId: Long): Device {
        return deviceRepository.findById(deviceId).orElseThrow { RuntimeException() }
    }

    override fun getAll(): List<Device> {
        return deviceRepository.findAll()
    }

    override fun update(device: Device): Device {
        return deviceRepository.save(device)
    }

    override fun deleteById(id: Long) {
        return deviceRepository.deleteById(id)
    }
}