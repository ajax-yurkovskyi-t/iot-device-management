package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.DELETE
import com.example.internal.NatsSubject.Device.DEVICE_QUEUE_GROUP
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.DeleteDeviceMapper
import com.example.iotmanagementdevice.service.device.DeviceService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DeleteDeviceNatsController
    (override val connection: Connection,
    private val deviceService: DeviceService,
    private val deleteDeviceMapper: DeleteDeviceMapper,
) : NatsController<DeleteDeviceRequest, DeleteDeviceResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = DELETE
    override val parser: Parser<DeleteDeviceRequest> = DeleteDeviceRequest.parser()

    override fun handle(request: DeleteDeviceRequest): Mono<DeleteDeviceResponse> {
        return deviceService.deleteById(request.id)
            .thenReturn(deleteDeviceMapper.successDeleteResponse())
            .onErrorResume {
                deleteDeviceMapper.failureDeleteResponse(it).toMono()
            }
    }
}