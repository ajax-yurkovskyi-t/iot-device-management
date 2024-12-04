package com.example.gateway.infrastructure.grpc.mapper

import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.springframework.stereotype.Component
import com.example.grpcapi.reqrep.device.GetDeviceByIdRequest as GrpcGetDeviceByIdRequest
import com.example.grpcapi.reqrep.device.GetDeviceByIdResponse as GrpcGetDeviceByIdResponse

@Component
class GetDeviceByIdGrpcMapper {
    fun toInternal(getDeviceByIdRequest: GrpcGetDeviceByIdRequest): GetDeviceByIdRequest {
        return GetDeviceByIdRequest.newBuilder().apply {
            id = getDeviceByIdRequest.id
        }.build()
    }

    @Suppress("TooGenericExceptionThrown")
    fun toGrpc(getDeviceByIdResponse: GetDeviceByIdResponse): GrpcGetDeviceByIdResponse {
        return when (getDeviceByIdResponse.responseCase!!) {
            GetDeviceByIdResponse.ResponseCase.SUCCESS -> GrpcGetDeviceByIdResponse.newBuilder().apply {
                successBuilder.device = getDeviceByIdResponse.success.device
            }.build()

            GetDeviceByIdResponse.ResponseCase.FAILURE -> toFailure(getDeviceByIdResponse)

            GetDeviceByIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun toFailure(response: GetDeviceByIdResponse): GrpcGetDeviceByIdResponse {
        val message = response.failure.message.orEmpty()
        return GrpcGetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.message = message
            when (response.failure.errorCase!!) {
                GetDeviceByIdResponse.Failure.ErrorCase.DEVICE_NOT_FOUND -> failureBuilder.deviceNotFoundBuilder
                GetDeviceByIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> {}
            }
        }.build()
    }
}
