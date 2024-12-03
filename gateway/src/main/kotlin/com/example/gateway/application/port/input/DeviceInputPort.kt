package com.example.gateway.application.port.input

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceInputPort {
    fun subscribeToUpdatesByUserId(userId: String): Flux<DeviceUpdatedEvent>
    fun getDevicesByUserId(request: GetDevicesByUserIdRequest): Mono<GetDevicesByUserIdResponse>
    fun getDeviceById(request: GetDeviceByIdRequest): Mono<GetDeviceByIdResponse>
    fun createDevice(request: CreateDeviceRequest): Mono<CreateDeviceResponse>
}
