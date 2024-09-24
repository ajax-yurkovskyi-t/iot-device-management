package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.model.DeviceStatusType
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.repository.DeviceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeviceServiceImplTest {

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    @MockK
    private lateinit var deviceMapper: DeviceMapper

    @InjectMockKs
    lateinit var deviceService: DeviceServiceImpl

    private lateinit var deviceRequestDto: DeviceCreateRequestDto
    private lateinit var deviceUpdateDto: DeviceUpdateRequestDto
    private lateinit var device: MongoDevice
    private lateinit var savedDevice: MongoDevice
    private lateinit var deviceResponseDto: DeviceResponseDto

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        deviceRequestDto = DeviceFixture.createDeviceCreateRequestDto()
        deviceUpdateDto = DeviceFixture.createDeviceUpdateRequestDto()
        device = DeviceFixture.createDevice()
        savedDevice = DeviceFixture.createSavedDevice()
        deviceResponseDto = DeviceFixture.createDeviceResponseDto()
    }

    @Test
    fun `should create a new device`() {
        // Stubbing
        every { deviceMapper.toEntity(deviceRequestDto) } returns device
        every { deviceRepository.save(device) } returns savedDevice
        every { deviceMapper.toDto(savedDevice) } returns deviceResponseDto

        // When
        val result = deviceService.create(deviceRequestDto)

        // Then
        assertEquals(deviceResponseDto, result)
        verify {
            deviceMapper.toEntity(deviceRequestDto)
            deviceRepository.save(device)
            deviceMapper.toDto(savedDevice)
        }
    }

    @Test
    fun `should throw exception when creating a device with invalid data`() {
        // Given
        val invalidRequestDto = deviceRequestDto.copy(name = "")

        // Stubbing
        every { deviceMapper.toEntity(invalidRequestDto) } returns device
        every { deviceRepository.save(device) } throws IllegalArgumentException("Invalid device data")

        // When
        val exception = assertThrows<IllegalArgumentException> {
            deviceService.create(invalidRequestDto)
        }

        // Then
        assertEquals("Invalid device data", exception.message)
        verify { deviceRepository.save(device) }
    }

    @Test
    fun `should throw exception when device not found by id`() {
        // Given
        val deviceId = ObjectId()

        // Stubbing
        every { deviceRepository.findById(deviceId) } returns null

        // When
        val exception = assertThrows<EntityNotFoundException> {
            deviceService.getById(deviceId.toString())
        }

        // Then
        assertEquals("Device with id $deviceId not found", exception.message)
        verify { deviceRepository.findById(deviceId) }
    }

    @Test
    fun `should return device by id`() {
        // Given
        val deviceId = ObjectId()
        val device = device.copy(id = deviceId)
        val responseDto = deviceResponseDto.copy(name = "Device1", description = "A test device", type = "Sensor")

        // Stubbing
        every { deviceRepository.findById(deviceId) } returns device
        every { deviceMapper.toDto(device) } returns responseDto

        // When
        val result = deviceService.getById(deviceId.toString())

        // Then
        assertEquals(responseDto, result)
        verify {
            deviceRepository.findById(deviceId)
            deviceMapper.toDto(device)
        }
    }

    @Test
    fun `should throw exception when updating a non-existing device`() {
        // Given
        val nonExistingDeviceId = ObjectId()

        // Stubbing
        every { deviceRepository.findById(nonExistingDeviceId) } returns null

        // When
        val exception = assertThrows<EntityNotFoundException> {
            deviceService.update(nonExistingDeviceId.toString(), deviceUpdateDto)
        }

        // Then
        assertEquals("Device with id $nonExistingDeviceId not found", exception.message)
        verify { deviceRepository.findById(nonExistingDeviceId) }
    }

    @Test
    fun `should return all devices`() {
        // Given
        val device2 = device.copy(
            id = ObjectId(),
            name = "Device2",
            description = "A test device 2",
            type = "Actuator",
            statusType = DeviceStatusType.OFFLINE
        )
        val responseDto2 = deviceResponseDto.copy(
            name = "Device2",
            description = "A test device 2",
            type = "Actuator",
            statusType = DeviceStatusType.OFFLINE
        )
        val deviceList = listOf(device, device2)
        val responseDtoList = listOf(deviceResponseDto, responseDto2)

        // Stubbing
        every { deviceRepository.findAll() } returns deviceList
        every { deviceMapper.toDto(device) } returns deviceResponseDto
        every { deviceMapper.toDto(device2) } returns responseDto2

        // When
        val result = deviceService.getAll()

        // Then
        assertEquals(responseDtoList, result)
        verify {
            deviceRepository.findAll()
            deviceMapper.toDto(device)
            deviceMapper.toDto(device2)
        }
    }

    @Test
    fun `should update device`() {
        // Given
        val deviceId = ObjectId()
        val existingDevice = device.copy(
            id = deviceId,
            name = "Old Name",
            description = "Old Description",
            type = "Old Type",
            statusType = DeviceStatusType.OFFLINE
        )
        val updatedDevice = existingDevice.copy(
            name = deviceUpdateDto.name,
            description = deviceUpdateDto.description,
            type = deviceUpdateDto.type,
            statusType = deviceUpdateDto.statusType
        )
        val updatedDeviceResponseDto = deviceResponseDto.copy(
            name = deviceUpdateDto.name,
            description = deviceUpdateDto.description,
            type = deviceUpdateDto.type,
            statusType = deviceUpdateDto.statusType
        )

        // Stubbing
        every { deviceRepository.findById(deviceId) } returns existingDevice
        every { deviceRepository.save(updatedDevice) } returns updatedDevice
        every { deviceMapper.toDto(updatedDevice) } returns updatedDeviceResponseDto

        // When
        val result = deviceService.update(deviceId.toString(), deviceUpdateDto)

        // Then
        assertEquals(updatedDeviceResponseDto, result)
        verify {
            deviceRepository.findById(deviceId)
            deviceRepository.save(updatedDevice)
            deviceMapper.toDto(updatedDevice)
        }
    }
}
