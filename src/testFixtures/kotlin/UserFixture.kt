import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.dto.device.response.DeviceStatusTypeResponse
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoRole
import com.example.iotmanagementdevice.model.MongoUser
import org.bson.types.ObjectId

object UserFixture {

    fun createUser(): MongoUser {
        return MongoUser(
            id = ObjectId(),
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            userPassword = "encodedPassword",
            roles = mutableSetOf(createRole()),
            devices = mutableListOf()
        )
    }

    fun createRole(): MongoRole {
        return MongoRole(id = ObjectId(), roleName = MongoRole.RoleName.USER)
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

    fun createUserResponseDto(devices: List<MongoDevice>): UserResponseDto {
        return UserResponseDto(
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            devices = devices.map { device ->
                DeviceResponseDto(device.name, device.description, device.type, DeviceStatusTypeResponse.ONLINE)
            }
        )
    }

    fun createDeviceResponseDto(device: MongoDevice): DeviceResponseDto {
        return DeviceResponseDto(
            name = device.name,
            description = device.description,
            type = device.type,
            statusType = DeviceStatusTypeResponse.ONLINE
        )
    }
}
