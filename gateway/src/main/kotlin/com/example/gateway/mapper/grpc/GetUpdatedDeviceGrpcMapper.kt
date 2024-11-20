package com.example.gateway.mapper.grpc

import com.example.commonmodels.device.Device
import com.example.core.exception.EntityNotFoundException
import com.example.grpcapi.reqrep.device.UpdatedDeviceResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.springframework.stereotype.Component

@Component
@Suppress("TooGenericExceptionThrown")
class GetUpdatedDeviceGrpcMapper {

    fun toUpdateDeviceResponseList(response: GetDevicesByUserIdResponse): List<UpdatedDeviceResponse> {
        return when (response.responseCase!!) {
            GetDevicesByUserIdResponse.ResponseCase.SUCCESS ->
                mapDeviceListToUpdateDeviceResponseList(response.success.devicesList)

            GetDevicesByUserIdResponse.ResponseCase.FAILURE -> toFailure(response)
            GetDevicesByUserIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    fun toGetUpdatedDeviceResponse(response: UpdateDeviceResponse): UpdatedDeviceResponse {
        val message = response.failure.message.orEmpty()
        return when (response.responseCase!!) {
            UpdateDeviceResponse.ResponseCase.SUCCESS -> UpdatedDeviceResponse.newBuilder().apply {
                successBuilder.device = response.success.device
            }.build()

            UpdateDeviceResponse.ResponseCase.FAILURE -> error(message)
            UpdateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun mapDeviceListToUpdateDeviceResponseList(devicesList: List<Device>): List<UpdatedDeviceResponse> =
        devicesList.map { device ->
            UpdatedDeviceResponse.newBuilder().apply {
                successBuilder.device = device
            }.build()
        }

    private fun toFailure(response: GetDevicesByUserIdResponse): Nothing {
        val message = response.failure.message.orEmpty()
        throw when (response.failure.errorCase!!) {
            GetDevicesByUserIdResponse.Failure.ErrorCase.USER_NOT_FOUND -> EntityNotFoundException(message)
            GetDevicesByUserIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}
