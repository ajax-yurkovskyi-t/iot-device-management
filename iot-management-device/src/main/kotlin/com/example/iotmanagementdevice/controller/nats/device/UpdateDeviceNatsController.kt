package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.UPDATE
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class UpdateDeviceNatsController(
    private val deviceService: DeviceService,
    private val updateDeviceMapper: UpdateDeviceMapper,
) : ProtoNatsMessageHandler<UpdateDeviceRequest, UpdateDeviceResponse> {

    override val log: Logger = LoggerFactory.getLogger(UpdateDeviceNatsController::class.java)
    override val parser: Parser<UpdateDeviceRequest> = UpdateDeviceRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject = UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateDeviceRequest?, e: Exception): Mono<UpdateDeviceResponse> {
        return updateDeviceMapper.toFailureUpdateDeviceResponse(e).toMono()
    }

    override fun doHandle(inMsg: UpdateDeviceRequest): Mono<UpdateDeviceResponse> {
        return deviceService.update(inMsg.id, updateDeviceMapper.toDeviceUpdateRequestDto(inMsg))
            .map { updateDeviceMapper.toUpdateDeviceResponse(it) }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
