package com.example.gateway.infrastructure.rest.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.springframework.stereotype.Component

@Component
class DeleteDeviceMapper {
    @Suppress("TooGenericExceptionThrown")
    fun toDeleteResponse(response: DeleteDeviceResponse) {
        val message = response.failure.message.orEmpty()
        when (response.responseCase!!) {
            DeleteDeviceResponse.ResponseCase.SUCCESS -> Unit
            DeleteDeviceResponse.ResponseCase.FAILURE -> error(message)
            DeleteDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
