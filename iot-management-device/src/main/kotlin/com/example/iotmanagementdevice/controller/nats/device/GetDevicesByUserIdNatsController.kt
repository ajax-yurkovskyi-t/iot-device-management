package com.example.iotmanagementdevice.controller.nats.device

import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.mapper.GetDevicesByUserIdMapper
import com.example.iotmanagementdevice.service.user.UserService
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetDevicesByUserIdNatsController(
    private val userService: UserService,
    private val getDevicesByUserIdMapper: GetDevicesByUserIdMapper,
) : ProtoNatsMessageHandler<GetDevicesByUserIdRequest, GetDevicesByUserIdResponse> {

    override val log: Logger = LoggerFactory.getLogger(GetDevicesByUserIdNatsController::class.java)
    override val parser: Parser<GetDevicesByUserIdRequest> = GetDevicesByUserIdRequest.parser()
    override val queue: String = DEVICE_QUEUE_GROUP
    override val subject = GET_BY_USER_ID

    override fun doOnUnexpectedError(
        inMsg: GetDevicesByUserIdRequest?,
        e: Exception,
    ): Mono<GetDevicesByUserIdResponse> {
        return getDevicesByUserIdMapper.toFailure(e).toMono()
    }

    override fun doHandle(inMsg: GetDevicesByUserIdRequest): Mono<GetDevicesByUserIdResponse> {
        return userService.getDevicesByUserId(inMsg.userId).collectList()
            .map { getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(it) }
    }

    companion object {
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"
    }
}
