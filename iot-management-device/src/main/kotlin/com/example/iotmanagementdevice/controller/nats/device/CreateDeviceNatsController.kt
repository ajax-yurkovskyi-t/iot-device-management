package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.CreateDeviceMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService,
    private val createDeviceMapper: CreateDeviceMapper,
) : NatsController<CreateDeviceRequest, CreateDeviceResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = CREATE
    override val parser: Parser<CreateDeviceRequest> = CreateDeviceRequest.parser()
    override val responseType: CreateDeviceResponse = CreateDeviceResponse.getDefaultInstance()

    override fun handle(request: CreateDeviceRequest): Mono<CreateDeviceResponse> {
        return deviceService.create(createDeviceMapper.toDeviceCreateRequestDto(request))
            .map { createDeviceMapper.toCreateDeviceResponse(it) }
            .onErrorResume { throwable ->
                createDeviceMapper.toFailureCreateDeviceResponse(throwable).toMono()
            }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
