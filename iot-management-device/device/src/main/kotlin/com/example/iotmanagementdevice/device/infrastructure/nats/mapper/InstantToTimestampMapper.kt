package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.google.protobuf.Timestamp
import org.mapstruct.Mapper
import java.time.Instant

@Mapper(componentModel = "spring")
abstract class InstantToTimestampMapper {
    fun mapInstantToTimestamp(instant: Instant): Timestamp {
        return Timestamp.newBuilder().apply {
            seconds = instant.epochSecond
            nanos = instant.nano
        }.build()
    }
}
