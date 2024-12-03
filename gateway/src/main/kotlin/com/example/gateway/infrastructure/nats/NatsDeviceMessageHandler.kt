package com.example.gateway.infrastructure.nats

import com.example.gateway.application.port.input.DeviceInputPort
import com.example.internal.NatsSubject
import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class NatsDeviceMessageHandler(
    private val natsMessagePublisher: NatsMessagePublisher,
    private val natsHandlerManager: NatsHandlerManager,
) : DeviceInputPort {
    override fun subscribeToUpdatesByUserId(userId: String): Flux<DeviceUpdatedEvent> {
        return natsHandlerManager.subscribe(NatsSubject.Device.updateByUserId(userId)) { message ->
            DeviceUpdatedEvent.parser().parseFrom(message.data)
        }
    }

    override fun getDevicesByUserId(request: GetDevicesByUserIdRequest): Mono<GetDevicesByUserIdResponse> {
        return natsMessagePublisher.request(GET_BY_USER_ID, request, GetDevicesByUserIdResponse.parser())
    }

    override fun getDeviceById(request: GetDeviceByIdRequest): Mono<GetDeviceByIdResponse> {
        return natsMessagePublisher.request(GET_BY_ID, request, GetDeviceByIdResponse.parser())
    }

    override fun createDevice(request: CreateDeviceRequest): Mono<CreateDeviceResponse> {
        return natsMessagePublisher.request(CREATE, request, CreateDeviceResponse.parser())
    }
}
