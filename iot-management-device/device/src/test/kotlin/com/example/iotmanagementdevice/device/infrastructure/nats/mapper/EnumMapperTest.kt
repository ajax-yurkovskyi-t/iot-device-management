package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.commonmodels.device.Device
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import com.example.iotmanagementdevice.device.domain.Device as DomainDevice

class EnumMapperTest {
    private val enumMapper = EnumMapperImpl()

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        ONLINE, STATUS_TYPE_ONLINE,
        OFFLINE, STATUS_TYPE_OFFLINE"""
    )
    fun `should return the correct proto DeviceStatusType`(
        statusType: DomainDevice.DeviceStatusType,
        expected: Device.StatusType
    ) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        STATUS_TYPE_UNSPECIFIED, OFFLINE,
        STATUS_TYPE_ONLINE, ONLINE,
        STATUS_TYPE_OFFLINE, OFFLINE,
        UNRECOGNIZED, OFFLINE"""
    )
    fun `should return the correct DeviceStatusType`(
        statusType: Device.StatusType,
        expected: DomainDevice.DeviceStatusType
    ) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(expected, result)
    }
}
