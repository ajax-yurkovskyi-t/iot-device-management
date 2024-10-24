import com.example.core.dto.DeviceStatusType
import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.iotmanagementdevice.model.MongoDevice
import org.bson.types.ObjectId

object DeviceFixture {

    fun createDeviceCreateRequestDto(): DeviceCreateRequestDto {
        return DeviceCreateRequestDto(
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DeviceStatusType.ONLINE
        )
    }

    fun createDeviceUpdateRequestDto(): DeviceUpdateRequestDto {
        return DeviceUpdateRequestDto(
            name = "New Name",
            description = "New Description",
            type = "New Type",
            statusType = DeviceStatusType.ONLINE
        )
    }

    fun createDevice(): MongoDevice {
        return MongoDevice(
            id = ObjectId(),
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = MongoDevice.DeviceStatusType.ONLINE,
            userId = null
        )
    }

    fun createSavedDevice(): MongoDevice {
        return createDevice().copy(id = ObjectId())
    }

    fun createDeviceResponseDto(): DeviceResponseDto {
        return DeviceResponseDto(
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DeviceStatusType.ONLINE
        )
    }

    fun createDeviceRequest(): CreateDeviceRequest = CreateDeviceRequest.newBuilder().apply {
        setName("ProtoDevice")
        setType("Sensor")
        setDescription("A test device")
        setStatusType(CreateDeviceRequest.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun deleteDeviceRequest(deviceId: String): DeleteDeviceRequest =
        DeleteDeviceRequest.newBuilder().setId(deviceId).build()

    fun getDeviceByIdRequest(deviceId: String): GetDeviceByIdRequest =
        GetDeviceByIdRequest.newBuilder().setId(deviceId).build()

    fun updateDeviceRequest(deviceId: String): UpdateDeviceRequest = UpdateDeviceRequest.newBuilder().apply {
        setId(deviceId)
        setName("ProtoDevice")
        setType("Sensor")
        setDescription("A test device")
        setStatusType(UpdateDeviceRequest.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun getAllDevicesRequest(): GetAllDevicesRequest =
        GetAllDevicesRequest.newBuilder().build()
}
