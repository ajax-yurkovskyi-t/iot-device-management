package com.example.iotmanagementdevice.device.application.service

import com.example.core.exception.EntityNotFoundException
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.iotmanagementdevice.device.DeviceFixture
import com.example.iotmanagementdevice.device.DeviceFixture.createDeviceCreate
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceMessageProducerOutPort
import com.example.iotmanagementdevice.device.domain.Device
import com.example.iotmanagementdevice.device.infrastructure.kafka.mapper.DeviceUpdateEventMapper
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class DeviceServiceTest {

    @MockK
    private lateinit var deviceRepositoryOutPort: DeviseRepositoryOutPort

    @MockK
    private lateinit var updateDeviceMessageProducerOutPort: UpdateDeviceMessageProducerOutPort

    @MockK
    private lateinit var deviceUpdateEventMapper: DeviceUpdateEventMapper

    @InjectMockKs
    lateinit var deviceService: DeviceService

    private lateinit var device: Device

    @BeforeEach
    fun setUp() {
        device = DeviceFixture.createDevice()
    }

    @Test
    fun `should create a new device`() {
        // Given
        val createDevice = createDeviceCreate()

        // Stubbing
        every { deviceRepositoryOutPort.save(createDevice) } returns device.toMono()

        // When
        val createdDevice = deviceService.create(createDevice)

        // Then
        createdDevice.test()
            .expectNext(device)
            .verifyComplete()

        verify {
            deviceRepositoryOutPort.save(createDevice)
        }
    }

    @Test
    fun `should return device by id`() {
        // Given
        val deviceId = ObjectId().toString()
        val device = device.copy(id = deviceId)

        // Stubbing
        every { deviceRepositoryOutPort.findById(deviceId) } returns device.toMono()

        // When
        val foundDevice = deviceService.getById(deviceId)

        // Then
        foundDevice.test()
            .expectNext(device)
            .verifyComplete()

        verify {
            deviceRepositoryOutPort.findById(deviceId)
        }
    }

    @Test
    fun `should throw exception when device not found by id`() {
        // Given
        val deviceId = ObjectId().toString()

        // Stubbing
        every { deviceRepositoryOutPort.findById(deviceId) } returns Mono.empty()

        // When
        val nonExistingUser = deviceService.getById(deviceId)

        // Then
        nonExistingUser.test()
            .verifyError<EntityNotFoundException>()

        verify { deviceRepositoryOutPort.findById(deviceId) }
    }

    @Test
    fun `should throw exception when updating a non-existing device`() {
        // Given
        val nonExistingDeviceId = String()

        // Stubbing
        every { deviceRepositoryOutPort.findById(nonExistingDeviceId) } returns Mono.empty()

        // When
        val nonExistentDevice = deviceService.getById(nonExistingDeviceId)

        // Then
        nonExistentDevice.test()
            .verifyError<EntityNotFoundException>()

        verify { deviceRepositoryOutPort.findById(nonExistingDeviceId) }
    }

    @Test
    fun `should return all devices`() {
        // Given
        val device2 = device.copy(
            id = ObjectId().toString(),
            name = "Device2",
            description = "A test device 2",
            type = "Actuator",
            statusType = Device.DeviceStatusType.OFFLINE
        )
        val deviceList = listOf(device, device2)

        // Stubbing
        every { deviceRepositoryOutPort.findAll() } returns deviceList.toFlux()

        // When
        val devices = deviceService.getAll()

        // Then
        devices.test()
            .expectNextSequence(deviceList)
            .verifyComplete()

        verify {
            deviceRepositoryOutPort.findAll()
        }
    }

    @Test
    fun `should return devices by userId`() {
        // Given
        val userId = ObjectId()
        val device2 = device.copy(id = ObjectId().toString())

        val deviceList = listOf(device, device2)

        // Stubbing
        every { deviceRepositoryOutPort.findDevicesByUserId(userId.toString()) } returns deviceList.toFlux()

        // When
        val devices = deviceService.getDevicesByUserId(userId.toString())

        // Then
        devices.test()
            .expectNextSequence(deviceList)
            .verifyComplete()

        verify { deviceRepositoryOutPort.findDevicesByUserId(userId.toString()) }
    }

    @Test
    fun `should update device`() {
        // Given
        val deviceId = ObjectId().toString()
        val existingDevice = device.copy(
            id = deviceId,
            name = "Old Name",
            description = "Old Description",
            type = "Old Type",
            statusType = Device.DeviceStatusType.OFFLINE
        )
        val updatedDevice = existingDevice.copy(
            name = "Updated Name",
            description = "Updated Description",
            type = "Updated Type",
            statusType = Device.DeviceStatusType.ONLINE
        )

        // Stubbing
        every { deviceRepositoryOutPort.findById(deviceId) } returns existingDevice.toMono()
        every { deviceRepositoryOutPort.save(updatedDevice) } returns updatedDevice.toMono()
        every { deviceUpdateEventMapper.toDeviceUpdatedEvent(updatedDevice) } returns
            DeviceUpdatedEvent.getDefaultInstance()
        every { updateDeviceMessageProducerOutPort.sendUpdateDeviceMessage(any()) } returns Unit.toMono()

        // When
        val updateResult = deviceService.update(deviceId, updatedDevice)

        // Then
        updateResult.test()
            .expectNext(updatedDevice)
            .verifyComplete()

        verify {
            deviceRepositoryOutPort.findById(deviceId)
            deviceRepositoryOutPort.save(updatedDevice)
        }
    }

    @Test
    fun `should delete device by id`() {
        // Given
        val deviceId = String()

        // Stubbing
        every { deviceRepositoryOutPort.deleteById(deviceId) } returns Mono.empty()

        // When
        val deletedDevice = deviceService.deleteById(deviceId)

        // Then
        deletedDevice.test()
            .verifyComplete()

        verify { deviceRepositoryOutPort.deleteById(deviceId) }
    }
}
