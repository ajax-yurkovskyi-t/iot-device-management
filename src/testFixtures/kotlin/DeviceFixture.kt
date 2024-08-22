import com.example.iot_management_device.dto.device.request.DeviceCreateRequestDto
import com.example.iot_management_device.dto.device.request.DeviceUpdateRequestDto
import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.model.Device
import com.example.iot_management_device.model.DeviceStatusType

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

    fun createDevice(): Device {
        return Device(
            id = null,
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DeviceStatusType.ONLINE,
            user = null
        )
    }

    fun createSavedDevice(): Device {
        return createDevice().copy(id = 1L)
    }

    fun createDeviceResponseDto(): DeviceResponseDto {
        return DeviceResponseDto(
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DeviceStatusType.ONLINE
        )
    }
}
