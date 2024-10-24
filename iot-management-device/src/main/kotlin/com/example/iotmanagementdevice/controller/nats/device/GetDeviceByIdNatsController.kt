package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.GetDeviceByIdMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetDeviceByIdNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService,
    private val getDeviceByIdMapper: GetDeviceByIdMapper,
) : NatsController<GetDeviceByIdRequest, GetDeviceByIdResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = GET_BY_ID
    override val parser: Parser<GetDeviceByIdRequest> = GetDeviceByIdRequest.parser()
    override val responseType: GetDeviceByIdResponse = GetDeviceByIdResponse.getDefaultInstance()

    override fun handle(request: GetDeviceByIdRequest): Mono<GetDeviceByIdResponse> {
        return deviceService.getById(request.id)
            .map { getDeviceByIdMapper.toGetDeviceByIdResponse(it) }
            .onErrorResume { throwable ->
                getDeviceByIdMapper.toFailureGetDeviceByIdResponse(throwable).toMono()
            }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
