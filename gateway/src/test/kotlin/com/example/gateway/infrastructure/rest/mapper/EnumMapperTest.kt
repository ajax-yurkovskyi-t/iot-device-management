package com.example.gateway.infrastructure.rest.mapper

import com.example.commonmodels.device.Device
import com.example.gateway.infrastructure.rest.dto.DeviceStatusType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class EnumMapperTest {
    private val enumMapper = EnumMapperImpl()

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        STATUS_TYPE_UNSPECIFIED, OFFLINE
        STATUS_TYPE_ONLINE, ONLINE
        STATUS_TYPE_OFFLINE, OFFLINE
        UNRECOGNIZED, OFFLINE"""
    )
    fun `should return the correct DeviceStatusType`(statusType: Device.StatusType, expected: DeviceStatusType) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(expected, result)
    }
}
