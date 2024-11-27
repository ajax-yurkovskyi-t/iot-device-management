package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.mapper.GetDeviceByIdMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetDeviceByIdNatsController(
    private val deviceService: DeviceService,
    private val getDeviceByIdMapper: GetDeviceByIdMapper,
) : ProtoNatsMessageHandler<GetDeviceByIdRequest, GetDeviceByIdResponse> {

    override val log: Logger = LoggerFactory.getLogger(GetDeviceByIdNatsController::class.java)
    override val parser: Parser<GetDeviceByIdRequest> = GetDeviceByIdRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject = GET_BY_ID

    override fun doOnUnexpectedError(inMsg: GetDeviceByIdRequest?, e: Exception): Mono<GetDeviceByIdResponse> {
        return getDeviceByIdMapper.toFailureGetDeviceByIdResponse(e).toMono()
    }

    override fun doHandle(inMsg: GetDeviceByIdRequest): Mono<GetDeviceByIdResponse> {
        return deviceService.getById(inMsg.id)
            .map { getDeviceByIdMapper.toGetDeviceByIdResponse(it) }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
