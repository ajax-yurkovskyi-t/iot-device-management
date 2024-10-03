import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.model.MongoDevice
import org.bson.types.ObjectId

object DeviceFixture {

    fun createDeviceCreateRequestDto(): DeviceCreateRequestDto {
        return DeviceCreateRequestDto(
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = MongoDevice.DeviceStatusType.ONLINE
        )
    }

    fun createDeviceUpdateRequestDto(): DeviceUpdateRequestDto {
        return DeviceUpdateRequestDto(
            name = "New Name",
            description = "New Description",
            type = "New Type",
            statusType = MongoDevice.DeviceStatusType.ONLINE
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
            statusType = MongoDevice.DeviceStatusType.ONLINE
        )
    }
}
