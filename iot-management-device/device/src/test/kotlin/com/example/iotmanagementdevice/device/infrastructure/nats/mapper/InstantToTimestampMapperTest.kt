package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.google.protobuf.Timestamp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantToTimestampMapperTest {
    private val instantMapper =
        InstantToTimestampMapperImpl()

    @Test
    fun `should map Instant to Timestamp`() {
        // Given
        val instant = Instant.ofEpochSecond(EPOCH_SECONDS, NANOS.toLong())
        val expectedTimestamp = Timestamp.newBuilder().apply {
            seconds = EPOCH_SECONDS
            nanos = NANOS
        }.build()

        // When
        val result = instantMapper.mapInstantToTimestamp(instant)

        // Then
        assertEquals(expectedTimestamp, result)
    }

    companion object {
        const val EPOCH_SECONDS = 1633072800L
        const val NANOS = 500_000_000
    }
}
