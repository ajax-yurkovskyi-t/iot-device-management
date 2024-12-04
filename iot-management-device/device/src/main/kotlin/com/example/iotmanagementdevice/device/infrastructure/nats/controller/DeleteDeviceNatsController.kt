package com.example.iotmanagementdevice.device.infrastructure.nats.controller

import com.example.internal.NatsSubject.Device.DELETE
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.device.application.port.input.DeviceServiceInPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.DeleteDeviceMapper
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class DeleteDeviceNatsController(
    private val deviceServiceInPort: DeviceServiceInPort,
    private val deleteDeviceMapper: DeleteDeviceMapper,
) : ProtoNatsMessageHandler<DeleteDeviceRequest, DeleteDeviceResponse> {

    override val log: Logger = LoggerFactory.getLogger(DeleteDeviceNatsController::class.java)
    override val parser: Parser<DeleteDeviceRequest> = DeleteDeviceRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject = DELETE

    override fun doOnUnexpectedError(inMsg: DeleteDeviceRequest?, e: Exception): Mono<DeleteDeviceResponse> {
        return deleteDeviceMapper.toFailureDeleteDeviceResponse(e).toMono()
    }

    override fun doHandle(inMsg: DeleteDeviceRequest): Mono<DeleteDeviceResponse> {
        return deviceServiceInPort.deleteById(inMsg.id)
            .thenReturn(deleteDeviceMapper.toSuccessDeleteResponse())
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
