package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.UPDATE
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class UpdateDeviceNatsController(
    override val connection: Connection,
    private val deviceService: DeviceService,
    private val updateDeviceMapper: UpdateDeviceMapper,
) : NatsController<UpdateDeviceRequest, UpdateDeviceResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = UPDATE
    override val parser: Parser<UpdateDeviceRequest> = UpdateDeviceRequest.parser()
    override val responseType: UpdateDeviceResponse = UpdateDeviceResponse.getDefaultInstance()

    override fun handle(request: UpdateDeviceRequest): Mono<UpdateDeviceResponse> {
        return deviceService.update(request.id, updateDeviceMapper.toDeviceUpdateRequestDto(request))
            .map { updateDeviceMapper.toUpdateDeviceResponse(it) }
            .onErrorResume { throwable ->
                updateDeviceMapper.toFailureUpdateDeviceResponse(throwable).toMono()
            }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
