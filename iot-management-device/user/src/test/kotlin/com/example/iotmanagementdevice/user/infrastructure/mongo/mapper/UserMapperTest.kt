package com.example.iotmanagementdevice.user.infrastructure.mongo.mapper

import com.example.iotmanagementdevice.user.UserFixture.createMongoUser
import com.example.iotmanagementdevice.user.UserFixture.createUser
import com.example.iotmanagementdevice.user.infrastructure.mongo.mapper.impl.UserMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UserMapperTest {

    private val userMapper = UserMapperImpl()

    @Test
    fun `should map mongo user to user`() {
        val user = createUser()
        val mongoUser = createMongoUser(user)

        val result = userMapper.toDomain(mongoUser)

        assertEquals(user, result)
    }

    @Test
    fun `should map user to mongo user`() {
        val user = createUser()
        val mongoUser = createMongoUser(user)

        val result = userMapper.toEntity(user)

        assertEquals(mongoUser, result)
    }
}