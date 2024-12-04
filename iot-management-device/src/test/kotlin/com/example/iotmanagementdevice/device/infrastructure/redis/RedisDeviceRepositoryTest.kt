package com.example.iotmanagementdevice.device.infrastructure.redis

import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.domain.Device
import com.example.iotmanagementdevice.device.infrastructure.redis.repository.RedisDeviceRepository.Companion.createDeviceKey
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import java.time.Duration

class RedisDeviceRepositoryTest : AbstractMongoTest {
    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    private lateinit var redisDeviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var mongoDeviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should find device by id and save cache from it`() {
        // GIVEN
        val savedDevice = mongoDeviceRepository.save(createDevice()).block()!!
        val key = createDeviceKey(savedDevice.id.toString())

        // WHEN
        redisDeviceRepository.findById(savedDevice.id.toString())

        // THEN
        val containsDeviceCache = redisTemplate.hasKey(key)

        redisTemplate.opsForValue().get(key)
            .test()
            .assertNext {
                assertEquals(savedDevice, objectMapper.readValue<Device>(it))
            }
            .verifyComplete()

        containsDeviceCache.test()
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `should find device by id when it is already cached`() {
        // GIVEN
        val device = createDevice()
        val key = createDeviceKey(device.id.toString())

        reactiveRedisTemplate.opsForValue().set(
            key,
            objectMapper.writeValueAsBytes(device),
            ttl
        ).block()!!

        // WHEN
        val foundDevice = redisDeviceRepository.findById(device.id.toString())

        // THEN
        foundDevice.test()
            .expectNext(device)
            .verifyComplete()
    }

    @Test
    fun `should set empty byte array when find device by invalid id for the first time and throw ex for second`() {
        // GIVEN
        val invalidDeviceId = ObjectId.get().toString()
        redisDeviceRepository.findById(invalidDeviceId).block()

        // WHEN
        val actualResponse = redisDeviceRepository.findById(invalidDeviceId)

        // THEN
        val emptyValueFromRedis = redisTemplate.opsForValue().get(createDeviceKey(invalidDeviceId))

        actualResponse.test()
            .verifyError<EntityNotFoundException>()

        emptyValueFromRedis.test()
            .expectNextMatches { it.isEmpty() }
            .verifyComplete()
    }

    @Test
    fun `should save device and save cache from it`() {
        // GIVEN
        val device = createDevice()
        val savedDevice = redisDeviceRepository.save(device).block()!!
        val containsDeviceCache = redisTemplate.hasKey(createDeviceKey(savedDevice.id.toString()))

        // WHEN
        val deviceById = redisDeviceRepository.findById(savedDevice.id.toString())

        // THEN
        deviceById.test()
            .expectNext(savedDevice)
            .verifyComplete()

        containsDeviceCache.test()
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `should delete device and delete it from cache`() {
        // GIVEN
        val device = createDevice()
        val savedDevice = redisDeviceRepository.save(device).block()!!
        val key = savedDevice.id.toString()

        // WHEN
        val deleteResult = redisDeviceRepository.deleteById(key)

        // THEN
        val doesCacheContainsDevice = redisTemplate.hasKey(key)

        deleteResult.test()
            .expectNext(Unit)
            .verifyComplete()

        doesCacheContainsDevice.test()
            .expectNext(false)
            .verifyComplete()
    }

    companion object {
        private val ttl = Duration.ofMinutes(3)
    }
}
