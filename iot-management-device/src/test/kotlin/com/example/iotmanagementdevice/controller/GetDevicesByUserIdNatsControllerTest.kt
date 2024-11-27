package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.getDevicesByUserIdRequest
import UserFixture.createUser
import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.GetDevicesByUserIdMapper
import com.example.iotmanagementdevice.repository.DeviceRepository
import com.example.iotmanagementdevice.repository.UserRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class GetDevicesByUserIdNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    @Qualifier("mongoDeviceRepository")
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var getDevicesByUserIdMapper: GetDevicesByUserIdMapper

    @Autowired
    private lateinit var deviceMapper: DeviceMapper

    @Test
    fun `should return existing user devices`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!
        val user = userRepository.save(createUser().copy(devices = listOf(device.id!!))).block()!!
        val deviceDto = deviceMapper.toDto(device)

        // WHEN
        val actual = doRequest(
            GET_BY_USER_ID,
            getDevicesByUserIdRequest(user.id.toString()),
            GetDevicesByUserIdResponse.parser()
        )

        // THEN
        assertEquals(getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(listOf(deviceDto)), actual)
    }

    @Test
    fun `should return message with EntityNotFoundException when user not found`() {
        // GIVEN
        val invalidId = ObjectId().toString()

        // WHEN
        val actual = doRequest(
            GET_BY_USER_ID,
            getDevicesByUserIdRequest(invalidId),
            GetDevicesByUserIdResponse.parser()
        )

        // THEN
        assertEquals(
            getDevicesByUserIdMapper.toFailure(
                EntityNotFoundException(
                    "User with id $invalidId not found"
                )
            ),
            actual
        )
    }
}
