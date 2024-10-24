package com.example.gateway.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class DeleteDeviceMapper {
    fun toDeleteResponse(response: DeleteDeviceResponse) {
        val message = response.failure.message.orEmpty()
        when (response.responseCase!!) {
            DeleteDeviceResponse.ResponseCase.SUCCESS -> Unit
            DeleteDeviceResponse.ResponseCase.FAILURE -> error(message)
            DeleteDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
