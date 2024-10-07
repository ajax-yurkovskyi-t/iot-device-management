package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val deviceMapper: DeviceMapper,
) : DeviceService {
    override fun create(requestDto: DeviceCreateRequestDto): Mono<DeviceResponseDto> {
        val device: MongoDevice = deviceMapper.toEntity(requestDto)
        return deviceRepository.save(device)
            .map { savedDevice -> deviceMapper.toDto(savedDevice) }
    }

    override fun getById(deviceId: String): Mono<DeviceResponseDto> {
        return deviceRepository.findById(deviceId)
            .switchIfEmpty { Mono.error(EntityNotFoundException("Device with id $deviceId not found")) }
            .map { device -> deviceMapper.toDto(device) }
    }

    override fun getAll(): Flux<DeviceResponseDto> {
        return deviceRepository.findAll().map { deviceMapper.toDto(it) }
    }

    override fun update(id: String, requestDto: DeviceUpdateRequestDto): Mono<DeviceResponseDto> {
        return deviceRepository.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("Device with id $id not found")) }
            .flatMap { existingDevice ->
                val updatedDevice = deviceMapper.toEntity(requestDto).copy(
                    id = existingDevice.id,
                    userId = existingDevice.userId
                )
                deviceRepository.save(updatedDevice)
            }
            .map { updatedDevice -> deviceMapper.toDto(updatedDevice) }
    }

    override fun deleteById(id: String): Mono<Unit> {
        return deviceRepository.deleteById(id)
    }
}
