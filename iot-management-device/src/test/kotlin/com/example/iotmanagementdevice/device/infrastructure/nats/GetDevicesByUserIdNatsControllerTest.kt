package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.getDevicesByUserIdRequest
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.GetDevicesByUserIdMapper
import com.example.iotmanagementdevice.user.UserFixture.createUser
import com.example.iotmanagementdevice.user.application.port.output.UserRepositoryOutPort
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class GetDevicesByUserIdNatsControllerTest : AbstractMongoTest {

    @Autowired
    private lateinit var deviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var userRepository: UserRepositoryOutPort

    @Autowired
    private lateinit var getDevicesByUserIdMapper: GetDevicesByUserIdMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return existing device`() {
        // GIVEN
        val user = userRepository.save(createUser()).block()!!
        val device = deviceRepository.save(createDevice().copy(userId = user.id)).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            GET_BY_USER_ID,
            getDevicesByUserIdRequest(user.id!!),
            GetDevicesByUserIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(listOf(device)))
            .verifyComplete()
    }
}
