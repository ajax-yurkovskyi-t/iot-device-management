package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.mapper.CreateDeviceMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class CreateDeviceNatsController(
    private val deviceService: DeviceService,
    private val createDeviceMapper: CreateDeviceMapper,
) : ProtoNatsMessageHandler<CreateDeviceRequest, CreateDeviceResponse> {

    override val log: Logger = LoggerFactory.getLogger(CreateDeviceNatsController::class.java)
    override val parser: Parser<CreateDeviceRequest> = CreateDeviceRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject: String = CREATE

    override fun doOnUnexpectedError(inMsg: CreateDeviceRequest?, e: Exception): Mono<CreateDeviceResponse> {
        return createDeviceMapper.toFailureCreateDeviceResponse(e).toMono()
    }

    override fun doHandle(inMsg: CreateDeviceRequest): Mono<CreateDeviceResponse> {
        return deviceService.create(createDeviceMapper.toDeviceCreateRequestDto(inMsg))
            .map { createDeviceMapper.toCreateDeviceResponse(it) }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
