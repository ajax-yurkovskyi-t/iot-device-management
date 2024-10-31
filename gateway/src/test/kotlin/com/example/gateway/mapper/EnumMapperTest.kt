package com.example.gateway.mapper

import com.example.core.dto.DeviceStatusType
import com.example.internal.commonmodels.Device
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class EnumMapperTest {
    private val enumMapper = EnumMapperImpl()

    @ParameterizedTest
    @MethodSource("provideStatusMappingCases")
    fun `should return the correct DeviceStatusType`(statusType: Device.StatusType, expected: DeviceStatusType) {
        // When
        val result = enumMapper.mapStatusType(statusType)

        // Then
        assertEquals(result, expected)
    }

    companion object {
        @JvmStatic
        fun provideStatusMappingCases(): List<Arguments> {
            return listOf(
                Arguments.of(Device.StatusType.STATUS_TYPE_UNSPECIFIED, DeviceStatusType.OFFLINE),
                Arguments.of(Device.StatusType.STATUS_TYPE_ONLINE, DeviceStatusType.ONLINE),
                Arguments.of(Device.StatusType.STATUS_TYPE_OFFLINE, DeviceStatusType.OFFLINE),
                Arguments.of(Device.StatusType.UNRECOGNIZED, DeviceStatusType.OFFLINE)
            )
        }
    }
}
