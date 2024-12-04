import com.example.commonmodels.device.Device
import com.example.gateway.infrastructure.rest.dto.DeviceStatusType
import com.example.gateway.infrastructure.rest.dto.request.DeviceCreateRequestDto
import com.example.gateway.infrastructure.rest.dto.request.DeviceUpdateRequestDto
import com.example.gateway.infrastructure.rest.dto.response.DeviceResponseDto
import com.example.grpcapi.reqrep.device.GetUpdatedDeviceRequest
import com.example.grpcapi.reqrep.device.StreamUpdatedDeviceResponse
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.grpcapi.reqrep.device.CreateDeviceRequest as GrpcCreateDeviceRequest
import com.example.grpcapi.reqrep.device.CreateDeviceResponse as GrpcCreateDeviceResponse
import com.example.grpcapi.reqrep.device.GetDeviceByIdRequest as GrpcGetDeviceByIdRequest
import com.example.grpcapi.reqrep.device.GetDeviceByIdResponse as GrpcGetDeviceByIdResponse

object DeviceProtoFixture {

    val deviceResponseDto = DeviceResponseDto(
        name = "Device",
        description = "A test device",
        type = "Sensor",
        statusType = DeviceStatusType.ONLINE
    )

    val deviceUpdateRequestDto = DeviceUpdateRequestDto(
        name = "Device",
        description = "A test device",
        type = "Sensor",
        statusType = DeviceStatusType.ONLINE
    )

    val deviceCreateRequestDto = DeviceCreateRequestDto(
        name = "Device",
        description = "A test device",
        type = "Sensor",
        statusType = DeviceStatusType.ONLINE
    )

    val deviceProto = Device.newBuilder().apply {
        name = "Device"
        description = ("A test device")
        type = ("Sensor")
        statusType = (Device.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun successfulCreateResponse(device: Device): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun failureCreateResponse(failureMessage: String): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun getDeviceByIdRequest(deviceId: String): GetDeviceByIdRequest {
        return GetDeviceByIdRequest.newBuilder().apply {
            id = deviceId
        }.build()
    }

    fun successfulGetDeviceByIdResponse(device: Device): GetDeviceByIdResponse {
        return GetDeviceByIdResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun failureGetDeviceByIdResponse(failureMessage: String): GetDeviceByIdResponse {
        return GetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun failureGrpcGetDeviceByIdResponse(failureMessage: String): GrpcGetDeviceByIdResponse {
        return GrpcGetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun successfulGetUpdatedDevicesResponse(device: Device): StreamUpdatedDeviceResponse {
        return StreamUpdatedDeviceResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun successfulUpdateResponse(device: Device): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun successfulDeviceUpdatedEvent(device: Device): DeviceUpdatedEvent {
        return DeviceUpdatedEvent.newBuilder().apply {
            this.device = device
        }.build()
    }

    fun failureUpdateDeviceResponse(failureMessage: String): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun successfulGetAllDevicesResponse(devices: List<Device>): GetAllDevicesResponse {
        return GetAllDevicesResponse.newBuilder().apply {
            successBuilder.addAllDevices(devices)
        }.build()
    }


    fun failureGetDevicesByUserIdResponse(failureMessage: String): GetDevicesByUserIdResponse {
        return GetDevicesByUserIdResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun grpcCreateDeviceRequest(): GrpcCreateDeviceRequest {
        return GrpcCreateDeviceRequest.newBuilder().apply {
            name = "Device"
            description = "A test device"
            type = "Sensor"
            statusType = Device.StatusType.STATUS_TYPE_ONLINE
        }.build()
    }

    fun createDeviceRequest(): CreateDeviceRequest {
        return CreateDeviceRequest.newBuilder().apply {
            name = "Device"
            description = "A test device"
            type = "Sensor"
            statusType = Device.StatusType.STATUS_TYPE_ONLINE
        }.build()
    }

    fun successfulGrpcCreateResponse(device: Device): GrpcCreateDeviceResponse {
        return GrpcCreateDeviceResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun grpcGetDeviceByIdRequest(deviceId: String): GrpcGetDeviceByIdRequest {
        return GrpcGetDeviceByIdRequest.newBuilder().apply {
            id = deviceId
        }.build()
    }

    fun successfulGrpcGetDeviceByIdResponse(device: Device): GrpcGetDeviceByIdResponse {
        return GrpcGetDeviceByIdResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun getUpdatedDevicesRequest(userId: String): GetUpdatedDeviceRequest {
        return GetUpdatedDeviceRequest.newBuilder().apply {
            this.userId = userId
        }.build()
    }

    fun getDevicesByUserIdRequest(userId: String): GetDevicesByUserIdRequest {
        return GetDevicesByUserIdRequest.newBuilder().apply {
            this.userId = userId
        }.build()
    }

    fun successfulGetDevicesByUserIdResponse(devices: List<Device>): GetDevicesByUserIdResponse {
        return GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addAllDevices(devices)
        }.build()
    }

    fun getUpdatedDeviceResponse(devicesList: List<Device>): List<StreamUpdatedDeviceResponse> =
        devicesList.map { device ->
            StreamUpdatedDeviceResponse.newBuilder().apply {
                successBuilder.device = device
            }.build()
        }
}
