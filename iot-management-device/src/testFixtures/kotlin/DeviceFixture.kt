import com.example.commonmodels.device.Device
import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.core.dto.DeviceStatusType
import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.iotmanagementdevice.model.MongoDevice
import com.google.protobuf.Timestamp
import org.bson.types.ObjectId
import java.time.Instant

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
            userId = ObjectId()
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
        setStatusType(Device.StatusType.STATUS_TYPE_ONLINE)
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
        setStatusType(Device.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun getAllDevicesRequest(): GetAllDevicesRequest =
        GetAllDevicesRequest.newBuilder().build()

    fun deviceUpdatedEvent(deviceId: String, userId: String, timestamp: Instant): DeviceUpdatedEvent {
        val device = Device.newBuilder().apply {
            id = deviceId
            this.userId = userId
            name = "defaultName"
            description = "defaultDescription"
            type = "defaultType"
            updatedAt = Timestamp.newBuilder().apply {
                seconds = timestamp.epochSecond
                nanos = timestamp.nano
            }.build()
        }.build()

        return DeviceUpdatedEvent.newBuilder().apply {
            this.device = device
        }.build()
    }

    fun updateDeviceNotification(deviceId: String, userId: String, timestamp: Instant): DeviceUpdateNotification {
        return DeviceUpdateNotification.newBuilder().apply {
            this.deviceId = deviceId
            this.userId = userId
            this.timestamp = Timestamp.newBuilder().apply {
                seconds = timestamp.epochSecond
                nanos = timestamp.nano
            }.build()
        }.build()
    }

    fun getDevicesByUserIdRequest(userId: String): GetDevicesByUserIdRequest {
        return GetDevicesByUserIdRequest.newBuilder().apply {
            this.userId = userId
        }.build()
    }

    fun getDevicesByUserIdResponse(list: List<DeviceResponseDto>): GetDevicesByUserIdResponse {
        return GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addAllDevices(
                list.map { dto ->
                    Device.newBuilder().apply {
                        name = dto.name
                        type = dto.type
                        description = dto.description
                        statusType = mapStatusType(dto.statusType!!)
                    }.build()
                }
            )
        }.build()
    }

    private fun mapStatusType(statusType: DeviceStatusType): Device.StatusType {
        return when (statusType) {
            DeviceStatusType.ONLINE -> Device.StatusType.STATUS_TYPE_ONLINE
            DeviceStatusType.OFFLINE -> Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }
}
