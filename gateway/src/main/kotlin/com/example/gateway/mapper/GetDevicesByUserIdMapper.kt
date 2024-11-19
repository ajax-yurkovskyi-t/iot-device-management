package com.example.gateway.mapper

import com.example.commonmodels.device.Device
import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.springframework.stereotype.Component

@Component
class GetDevicesByUserIdMapper {

    @Suppress("TooGenericExceptionThrown")
    fun toUpdateDeviceResponseList(response: GetDevicesByUserIdResponse): List<UpdateDeviceResponse> {
        return when (response.responseCase!!) {
            GetDevicesByUserIdResponse.ResponseCase.SUCCESS ->
                mapDevicesToUpdateDeviceResponses(response.success.devicesList)

            GetDevicesByUserIdResponse.ResponseCase.FAILURE -> toFailure(response)
            GetDevicesByUserIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun mapDevicesToUpdateDeviceResponses(devicesList: List<Device>): List<UpdateDeviceResponse> =
        devicesList.map { device ->
            UpdateDeviceResponse.newBuilder().apply {
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
