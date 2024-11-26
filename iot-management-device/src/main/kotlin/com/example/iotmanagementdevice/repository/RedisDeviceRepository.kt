package com.example.iotmanagementdevice.repository

import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.model.MongoDevice
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.lettuce.core.RedisException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.util.retry.Retry
import java.net.SocketException
import java.time.Duration

@Repository
@Primary
class RedisDeviceRepository(
    @Value("\${spring.data.redis.ttl.minutes}")
    private val redisExpirationTimeoutInMinutes: Long,
    private val mongoDeviceRepository: MongoDeviceRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val mapper: ObjectMapper,
) : DeviceRepository by mongoDeviceRepository {

    override fun save(device: MongoDevice): Mono<MongoDevice> {
        return mongoDeviceRepository.save(device).doOnSuccess {
            saveDeviceWithRetries(it)
        }
    }

    override fun findById(deviceId: String): Mono<MongoDevice> {
        val key = createDeviceKey(deviceId)
        return reactiveRedisTemplate.opsForValue().get(key)
            .handle { item, sink ->
                if (item.isEmpty()) {
                    sink.error(EntityNotFoundException("Device with id $deviceId not found"))
                } else {
                    runCatching { mapper.readValue<MongoDevice>(item) }
                        .onSuccess { sink.next(it) }
                        .onFailure {
                            reactiveRedisTemplate.unlink(key)
                            sink.error(it)
                        }
                }
            }
            .switchIfEmpty { findInMongoAndWriteToRedis(deviceId) }
            .onErrorResume(::isErrorFromRedis) {
                mongoDeviceRepository.findById(deviceId)
            }
    }

    override fun deleteById(deviceId: String): Mono<Unit> {
        val key = createDeviceKey(deviceId)
        return mongoDeviceRepository.deleteById(deviceId)
            .flatMap {
                reactiveRedisTemplate.unlink(key)
                    .onErrorResume(::isErrorFromRedis) {
                        log.warn("Redis failed to remove user from cache", it)
                        Mono.empty()
                    }.thenReturn(Unit)
            }.thenReturn(Unit)
    }

    private fun saveDeviceToRedis(device: MongoDevice): Mono<MongoDevice> {
        val key = createDeviceKey(device.id.toString())
        val byteArray = mapper.writeValueAsBytes(device)
        return reactiveRedisTemplate.opsForValue().set(
            key,
            byteArray,
            Duration.ofMinutes(redisExpirationTimeoutInMinutes)
        ).thenReturn(device)
    }

    private fun saveDeviceWithRetries(device: MongoDevice) {
        saveDeviceToRedis(device).retryWhen(
            Retry.backoff(MAX_RETRIES, Duration.ofSeconds(RETRY_BACKOFF_SECONDS))
                .filter { error -> isErrorFromRedis(error) }
                .doBeforeRetry { retrySignal ->
                    log.warn(
                        "Retrying saving device to Redis because of {}:",
                        retrySignal.failure().message
                    )
                }
        )
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun findInMongoAndWriteToRedis(deviceId: String): Mono<MongoDevice> {
        val key = createDeviceKey(deviceId)
        return mongoDeviceRepository.findById(deviceId).flatMap {
            saveDeviceToRedis(it)
        }
            .switchIfEmpty {
                reactiveRedisTemplate.opsForValue()
                    .set(key, byteArrayOf(), Duration.ofMinutes(redisExpirationTimeoutInMinutes))
                    .then(Mono.empty())
            }
    }

    private fun isErrorFromRedis(throwable: Throwable): Boolean {
        return redisErrors.any { it.isInstance(throwable) }
    }

    companion object {
        private const val KEY_PREFIX = "device"
        private val log = LoggerFactory.getLogger(RedisDeviceRepository::class.java)
        private const val MAX_RETRIES = 5L
        private const val RETRY_BACKOFF_SECONDS = 2L

        private val redisErrors = setOf(
            RedisConnectionFailureException::class,
            RedisException::class,
            SocketException::class,
            QueryTimeoutException::class,
        )

        fun createDeviceKey(key: String): String {
            return "$KEY_PREFIX$key"
        }
    }
}
