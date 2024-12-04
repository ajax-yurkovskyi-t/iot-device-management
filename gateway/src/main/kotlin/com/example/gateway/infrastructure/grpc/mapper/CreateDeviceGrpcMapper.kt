package com.example.gateway.infrastructure.grpc.mapper

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.springframework.stereotype.Component
import com.example.grpcapi.reqrep.device.CreateDeviceRequest as GrpcCreateDeviceRequest
import com.example.grpcapi.reqrep.device.CreateDeviceResponse as GrpcCreateDeviceResponse

@Component
class CreateDeviceGrpcMapper {
    fun toInternal(createDeviceRequest: GrpcCreateDeviceRequest): CreateDeviceRequest {
        return CreateDeviceRequest.newBuilder().apply {
            name = createDeviceRequest.name
            description = createDeviceRequest.description
            type = createDeviceRequest.type
            statusType = createDeviceRequest.statusType
        }.build()
    }

    @Suppress("TooGenericExceptionThrown")
    fun toGrpc(createDeviceResponse: CreateDeviceResponse): GrpcCreateDeviceResponse {
        val message = createDeviceResponse.failure.message.orEmpty()
        return when (createDeviceResponse.responseCase!!) {
            CreateDeviceResponse.ResponseCase.SUCCESS -> GrpcCreateDeviceResponse.newBuilder().apply {
                successBuilder.device = createDeviceResponse.success.device
            }.build()

            CreateDeviceResponse.ResponseCase.FAILURE -> error(message)
            CreateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
