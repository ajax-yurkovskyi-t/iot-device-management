package com.example.iotmanagementdevice.repository

import DeviceFixture.createDevice
import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.repository.RedisDeviceRepository.Companion.createDeviceKey
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

class RedisDeviceRepositoryTest : AbstractMongoTest {
    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    private lateinit var redisDeviceRepository: DeviceRepository

    @Test
    fun `should find device by id and save cache from it`() {
        // GIVEN
        val savedDevice = redisDeviceRepository.save(createDevice()).block()!!
        val key = createDeviceKey(savedDevice.id.toString())

        // WHEN
        val device = redisDeviceRepository.findById(savedDevice.id.toString())

        // THEN
        val containsDeviceCache = redisTemplate.hasKey(key)

        device.test()
            .expectNext(savedDevice)
            .verifyComplete()

        containsDeviceCache.test()
            .expectNext(true)
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

        // WHEN
        val deviceById = redisDeviceRepository.findById(savedDevice.id.toString())

        // THEN
        val containsDeviceCache = redisTemplate.hasKey(createDeviceKey(savedDevice.id.toString()))

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
}
