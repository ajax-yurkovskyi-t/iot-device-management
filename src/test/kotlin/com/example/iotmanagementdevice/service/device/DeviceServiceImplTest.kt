package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.DeviceStatusType
import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.repository.DeviceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

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
        every { deviceRepository.save(device) } returns savedDevice.toMono()
        every { deviceMapper.toDto(savedDevice) } returns deviceResponseDto

        // When
        val createdDevice = deviceService.create(deviceRequestDto)

        // Then
        createdDevice.test()
            .expectNext(deviceResponseDto)
            .verifyComplete()

        verify {
            deviceMapper.toEntity(deviceRequestDto)
            deviceRepository.save(device)
            deviceMapper.toDto(savedDevice)
        }
    }

    @Test
    fun `should return device by id`() {
        // Given
        val deviceId = ObjectId()
        val device = device.copy(id = deviceId)
        val responseDto = deviceResponseDto.copy(name = "Device1", description = "A test device", type = "Sensor")

        // Stubbing
        every { deviceRepository.findById(deviceId.toString()) } returns device.toMono()
        every { deviceMapper.toDto(device) } returns responseDto

        // When
        val foundDevice = deviceService.getById(deviceId.toString())

        // Then
        foundDevice.test()
            .expectNext(responseDto)
            .verifyComplete()

        verify {
            deviceRepository.findById(deviceId.toString())
            deviceMapper.toDto(device)
        }
    }

    @Test
    fun `should throw exception when device not found by id`() {
        // Given
        val deviceId = ObjectId()

        // Stubbing
        every { deviceRepository.findById(deviceId.toString()) } returns Mono.empty()

        // When
        val nonExistingUser = deviceService.getById(deviceId.toString())

        // Then
        nonExistingUser.test()
            .verifyError<EntityNotFoundException>()

        verify { deviceRepository.findById(deviceId.toString()) }
    }

    @Test
    fun `should throw exception when updating a non-existing device`() {
        // Given
        val nonExistingDeviceId = String()

        // Stubbing
        every { deviceRepository.findById(nonExistingDeviceId) } returns Mono.empty()

        // When
        val nonExistentDevice = deviceService.getById(nonExistingDeviceId)

        // Then
        nonExistentDevice.test()
            .verifyError<EntityNotFoundException>()

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
            statusType = MongoDevice.DeviceStatusType.OFFLINE
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
        every { deviceRepository.findAll() } returns deviceList.toFlux()
        every { deviceMapper.toDto(device) } returns deviceResponseDto
        every { deviceMapper.toDto(device2) } returns responseDto2

        // When
        val devices = deviceService.getAll()

        // Then
        devices.test()
            .expectNextSequence(responseDtoList)
            .verifyComplete()

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
            statusType = MongoDevice.DeviceStatusType.OFFLINE
        )
        val updatedDevice = existingDevice.copy(
            name = deviceUpdateDto.name,
            description = deviceUpdateDto.description,
            type = deviceUpdateDto.type,
            statusType = MongoDevice.DeviceStatusType.ONLINE
        )
        val updatedDeviceResponseDto = deviceResponseDto.copy(
            name = deviceUpdateDto.name,
            description = deviceUpdateDto.description,
            type = deviceUpdateDto.type,
            statusType = DeviceStatusType.ONLINE
        )

        // Stubbing
        every { deviceRepository.findById(deviceId.toString()) } returns existingDevice.toMono()
        every { deviceMapper.toEntity(deviceUpdateDto) } returns updatedDevice
        every { deviceRepository.save(updatedDevice) } returns updatedDevice.toMono()
        every { deviceMapper.toDto(updatedDevice) } returns updatedDeviceResponseDto

        // When
        val updateResult = deviceService.update(deviceId.toString(), deviceUpdateDto)

        // Then
        updateResult.test()
            .expectNext(updatedDeviceResponseDto)
            .verifyComplete()

        verify {
            deviceRepository.findById(deviceId.toString())
            deviceMapper.toEntity(deviceUpdateDto)
            deviceRepository.save(updatedDevice)
            deviceMapper.toDto(updatedDevice)
        }
    }

    @Test
    fun `should delete device by id`() {
        // Given
        val deviceId = String()

        // Stubbing
        every { deviceRepository.deleteById(deviceId) } returns Mono.empty()

        // When
        val deletedDevice = deviceService.deleteById(deviceId)

        // Then
        deletedDevice.test()
            .verifyComplete()

        verify { deviceRepository.deleteById(deviceId) }
    }
}
