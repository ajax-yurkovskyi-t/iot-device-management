package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_ALL
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.GetAllDevicesMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetAllDevicesNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService,
    private val getAllDevicesMapper: GetAllDevicesMapper,
) : NatsController<GetAllDevicesRequest, GetAllDevicesResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = GET_ALL
    override val parser: Parser<GetAllDevicesRequest> = GetAllDevicesRequest.parser()
    override val responseType: GetAllDevicesResponse = GetAllDevicesResponse.getDefaultInstance()

    override fun handle(request: GetAllDevicesRequest): Mono<GetAllDevicesResponse> {
        return deviceService.getAll()
            .collectList()
            .map { getAllDevicesMapper.toGetAllDevicesResponse(it) }
            .onErrorResume { throwable ->
                getAllDevicesMapper.toFailureGetAllDevicesResponse(throwable).toMono()
            }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}