package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.controller.nats.NatsController
import com.example.iotmanagementdevice.mapper.GetDevicesByUserIdMapper
import com.example.iotmanagementdevice.service.user.UserService
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetDevicesByUserIdNatsController(
    override val connection: Connection,
    private val userService: UserService,
    private val getDevicesByUserIdMapper: GetDevicesByUserIdMapper,
) : NatsController<GetDevicesByUserIdRequest, GetDevicesByUserIdResponse> {

    override val queueGroup: String = DEVICE_QUEUE_GROUP
    override val subject = GET_BY_USER_ID
    override val parser: Parser<GetDevicesByUserIdRequest> = GetDevicesByUserIdRequest.parser()
    override val responseType: GetDevicesByUserIdResponse = GetDevicesByUserIdResponse.getDefaultInstance()

    override fun handle(request: GetDevicesByUserIdRequest): Mono<GetDevicesByUserIdResponse> {
        return userService.getDevicesByUserId(request.userId).collectList()
            .map { getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(it) }
            .onErrorResume { throwable ->
                getDevicesByUserIdMapper.toFailure(throwable).toMono()
            }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
