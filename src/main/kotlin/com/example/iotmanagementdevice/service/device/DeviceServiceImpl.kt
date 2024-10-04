package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.springframework.stereotype.Service

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val deviceMapper: DeviceMapper,
) : DeviceService {
    override fun create(requestDto: DeviceCreateRequestDto): DeviceResponseDto {
        val device: MongoDevice = deviceMapper.toEntity(requestDto)
        return deviceMapper.toDto(deviceRepository.save(device))
    }

    override fun getById(deviceId: String): DeviceResponseDto {
        return deviceMapper.toDto(
            deviceRepository.findById(deviceId)
                ?: throw EntityNotFoundException("Device with id $deviceId not found")
        )
    }

    override fun getAll(): List<DeviceResponseDto> {
        return deviceRepository.findAll().map { deviceMapper.toDto(it) }
    }

    override fun update(id: String, requestDto: DeviceUpdateRequestDto): DeviceResponseDto {
        val existingDevice =
            deviceRepository.findById(id) ?: throw EntityNotFoundException("Device with id $id not found")

        val updatedDevice = deviceMapper.toEntity(requestDto).copy(
            id = existingDevice.id,
            userId = existingDevice.userId
        )

        return deviceMapper.toDto(deviceRepository.save(updatedDevice))
    }

    override fun deleteById(id: String) {
        return deviceRepository.deleteById(id)
    }
}
