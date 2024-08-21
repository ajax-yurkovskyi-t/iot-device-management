import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.dto.user.request.UserRegistrationRequestDto
import com.example.iot_management_device.dto.user.request.UserUpdateRequestDto
import com.example.iot_management_device.dto.user.response.UserResponseDto
import com.example.iot_management_device.model.Device
import com.example.iot_management_device.model.DeviceStatusType
import com.example.iot_management_device.model.Role
import com.example.iot_management_device.model.User

object UserFixture {

    fun createUser(): User {
        return User(
            id = null,
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            userPassword = "encodedPassword",
            roles = mutableSetOf(createRole()),
            devices = mutableListOf()
        )
    }

    fun createRole(): Role {
        return Role(id = 1L, roleName = Role.RoleName.USER)
    }

    fun createDevice(user: User): Device {
        return Device(
            id = 1L,
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DeviceStatusType.ONLINE,
            user = user
        )
    }

    fun createUserResponseDto(devices: List<Device>): UserResponseDto {
        return UserResponseDto(
            id = 1L,
            username = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            devices = devices.map { device ->
                DeviceResponseDto(device.name, device.description, device.type, device.statusType)
            }
        )
    }
}
