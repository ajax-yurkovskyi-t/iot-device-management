package com.example.iot_management_device.service.device

import com.example.iot_management_device.dto.device.request.DeviceRequestDto
import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.dto.device.request.DeviceUpdateRequestDto
import com.example.iot_management_device.exception.EntityNotFoundException
import com.example.iot_management_device.mapper.DeviceMapper
import com.example.iot_management_device.model.Device
import com.example.iot_management_device.repository.DeviceRepository
import org.springframework.stereotype.Service

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val deviceMapper: DeviceMapper,
) : DeviceService {
    override fun create(requestDto: DeviceRequestDto): DeviceResponseDto {
        val device: Device = deviceMapper.toEntity(requestDto)
        return deviceMapper.toDto(deviceRepository.save(device))
    }

    override fun getById(deviceId: Long): DeviceResponseDto {
        return deviceMapper.toDto(
            deviceRepository.findById(deviceId)
                .orElseThrow { EntityNotFoundException("Device with id $deviceId not found") })
    }

    override fun getAll(): List<DeviceResponseDto> {
        return deviceRepository.findAll().map { deviceMapper.toDto(it) }
    }

    override fun update(id: Long, requestDto: DeviceUpdateRequestDto): DeviceResponseDto {
        val existingDevice = deviceRepository.findById(id).orElseThrow {
            EntityNotFoundException("Device with id $id not found")
        }

        val updatedDevice = existingDevice.copy(
            name = requestDto.name,
            description = requestDto.description,
            type = requestDto.type,
            statusType = requestDto.statusType,
        )

        return deviceMapper.toDto(deviceRepository.save(updatedDevice))
    }

    override fun deleteById(id: Long) {
        return deviceRepository.deleteById(id)
    }
}
