import com.example.core.dto.DeviceStatusType
import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.commonmodels.Device
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse

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

    val deviceProto = Device.newBuilder()
        .setName("Device")
        .setDescription("A test device")
        .setType("Sensor")
        .setStatusType(Device.StatusType.ONLINE)
        .build()

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

    fun successfulUpdateResponse(device: Device): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            successBuilder.device = device
        }.build()
    }

    fun failureUpdateDeviceResponse(failureMessage: String): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.message = failureMessage
        }.build()
    }

    fun successfulGetAllDevicesResponse(devices: List<Device>): GetAllDevicesResponse {
        return GetAllDevicesResponse.newBuilder().apply {
            devices.forEach { device ->
                (successBuilder.addDevices(device))
            }
        }.build()
    }
}