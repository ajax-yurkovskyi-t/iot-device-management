package com.example.iotmanagementdevice.device.application.service

import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.device.application.port.input.DeviceServiceInPort
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceMessageProducerOutPort
import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class DeviceService(
    private val deviceRepositoryOutPort: DeviseRepositoryOutPort,
    private val updateDeviceMessageProducerOutPort: UpdateDeviceMessageProducerOutPort,
) : DeviceServiceInPort {

    override fun create(newDevice: CreateDevice): Mono<Device> {
        return deviceRepositoryOutPort.save(newDevice)
    }

    override fun getById(deviceId: String): Mono<Device> {
        return deviceRepositoryOutPort.findById(deviceId)
            .switchIfEmpty {
                Mono.error(EntityNotFoundException("Device with id $deviceId not found"))
            }
    }

    override fun getDevicesByUserId(userId: String): Flux<Device> {
        return deviceRepositoryOutPort.findDevicesByUserId(userId)
    }

    override fun getAll(): Flux<Device> {
        return deviceRepositoryOutPort.findAll()
    }

    override fun update(id: String, updateDevice: Device): Mono<Device> {
        return deviceRepositoryOutPort.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("Device with id $id not found")) }
            .flatMap { existingDevice ->
                val updatedDevice = updateDevice.copy(
                    id = existingDevice.id,
                    userId = existingDevice.userId
                )
                deviceRepositoryOutPort.save(updatedDevice)
            }
            .flatMap { updatedDevice ->
                updateDeviceMessageProducerOutPort.sendUpdateDeviceMessage(updatedDevice)
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
    }

    override fun deleteById(id: String): Mono<Unit> {
        return deviceRepositoryOutPort.deleteById(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeviceService::class.java)
    }
}
