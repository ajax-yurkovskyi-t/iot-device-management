package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_ALL
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.iotmanagementdevice.mapper.GetAllDevicesMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetAllDevicesNatsController(
    private val deviceService: DeviceService,
    private val getAllDevicesMapper: GetAllDevicesMapper,
) : ProtoNatsMessageHandler<GetAllDevicesRequest, GetAllDevicesResponse> {

    override val log: Logger = LoggerFactory.getLogger(GetAllDevicesNatsController::class.java)
    override val parser: Parser<GetAllDevicesRequest> = GetAllDevicesRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject = GET_ALL

    override fun doOnUnexpectedError(inMsg: GetAllDevicesRequest?, e: Exception): Mono<GetAllDevicesResponse> {
        return getAllDevicesMapper.toFailureGetAllDevicesResponse(e).toMono()
    }

    override fun doHandle(inMsg: GetAllDevicesRequest): Mono<GetAllDevicesResponse> {
        return deviceService.getAll()
            .collectList()
            .map { getAllDevicesMapper.toGetAllDevicesResponse(it) }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
