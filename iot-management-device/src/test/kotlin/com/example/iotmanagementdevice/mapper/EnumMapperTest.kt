package com.example.iotmanagementdevice.mapper

import com.example.commonmodels.device.Device
import com.example.core.dto.DeviceStatusType
import com.example.iotmanagementdevice.model.MongoDevice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class EnumMapperTest {
    private val enumMapper = EnumMapperImpl()

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        ONLINE, STATUS_TYPE_ONLINE,
        OFFLINE, STATUS_TYPE_OFFLINE"""
    )
    fun `should return the correct proto DeviceStatusType`(statusType: DeviceStatusType, expected: Device.StatusType) {
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
    fun `should return the correct DeviceStatusType`(statusType: Device.StatusType, expected: DeviceStatusType) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        textBlock = """
        ONLINE, STATUS_TYPE_ONLINE,
        OFFLINE, STATUS_TYPE_OFFLINE"""
    )
    fun `should return the correct proto DeviceStatusType from MongoDevice status`(
        statusType: MongoDevice.DeviceStatusType,
        expected: Device.StatusType
    ) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(expected, result)
    }
}
