package com.example.iotmanagementdevice.service.device

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.kafka.producer.DeviceUpdateProducer
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class DeviceServiceImpl(
    private val deviceRepository: DeviceRepository,
    private val deviceMapper: DeviceMapper,
    private val deviceUpdateProducer: DeviceUpdateProducer,
    private val updateDeviceMapper: UpdateDeviceMapper,
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
            .flatMap { updatedDevice ->
                deviceUpdateProducer.sendMessage(updateDeviceMapper.toUpdateDeviceResponse(updatedDevice))
                    .thenReturn(updatedDevice)
                    .onErrorResume { error ->
                        log.error(
                            "Failed to send device update message for device {}",
                            updatedDevice,
                            error
                        )
                        updatedDevice.toMono()
                    }
            }
            .map { updatedDevice -> deviceMapper.toDto(updatedDevice) }
    }

    override fun deleteById(id: String): Mono<Unit> {
        return deviceRepository.deleteById(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeviceServiceImpl::class.java)
    }
}
