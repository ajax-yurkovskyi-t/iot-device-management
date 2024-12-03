package com.example.gateway.infrastructure.grpc.mapper

import com.example.commonmodels.device.Device
import com.example.grpcapi.reqrep.device.StreamUpdatedDeviceResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import org.springframework.stereotype.Component

@Component
@Suppress("TooGenericExceptionThrown")
class GetUpdatedDeviceGrpcMapper {

    fun toUpdateDeviceResponseList(response: GetDevicesByUserIdResponse): List<StreamUpdatedDeviceResponse> {
        return when (response.responseCase!!) {
            GetDevicesByUserIdResponse.ResponseCase.SUCCESS ->
                mapDeviceListToUpdateDeviceResponseList(response.success.devicesList)

            GetDevicesByUserIdResponse.ResponseCase.FAILURE -> listOf(toFailure(response))

            GetDevicesByUserIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    fun toUpdatedDeviceResponse(event: DeviceUpdatedEvent): StreamUpdatedDeviceResponse {
        return StreamUpdatedDeviceResponse.newBuilder().apply {
            successBuilder.device = event.device
        }.build()
    }

    private fun mapDeviceListToUpdateDeviceResponseList(devicesList: List<Device>): List<StreamUpdatedDeviceResponse> =
        devicesList.map { device ->
            StreamUpdatedDeviceResponse.newBuilder().apply {
                successBuilder.device = device
            }.build()
        }

    private fun toFailure(response: GetDevicesByUserIdResponse): StreamUpdatedDeviceResponse {
        val message = response.failure.message.orEmpty()
        return StreamUpdatedDeviceResponse.newBuilder().apply {
            failureBuilder.message = message
            when (response.failure.errorCase!!) {
                GetDevicesByUserIdResponse.Failure.ErrorCase.USER_NOT_FOUND -> failureBuilder.userNotFoundBuilder
                GetDevicesByUserIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> {}
            }
        }.build()
    }
}
