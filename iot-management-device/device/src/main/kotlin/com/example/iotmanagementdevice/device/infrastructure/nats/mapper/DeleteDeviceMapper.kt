package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.springframework.stereotype.Component

@Component
class DeleteDeviceMapper {
    fun toSuccessDeleteResponse(): DeleteDeviceResponse {
        return DeleteDeviceResponse.newBuilder().apply {
            successBuilder
        }.build()
    }

    fun toFailureDeleteDeviceResponse(throwable: Throwable): DeleteDeviceResponse {
        val message = throwable.message.orEmpty()
        return DeleteDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(message)
        }.build()
    }
}
