package com.example.iotmanagementdevice.user

import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.user.domain.CreateUser
import com.example.iotmanagementdevice.user.domain.User
import com.example.iotmanagementdevice.user.infrastructure.mongo.entity.MongoUser
import org.bson.types.ObjectId

object UserFixture {

    fun createUser(): User {
        return User(
            id = ObjectId().toString(),
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            userPassword = "encodedPassword",
            roles = mutableSetOf(createRole()),
            devices = mutableListOf()
        )
    }

    fun createUserCreate(): CreateUser {
        return CreateUser(
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            userPassword = "encodedPassword",
            roles = mutableSetOf(createRole()),
        )
    }

    fun createMongoUser(user: User): MongoUser {
        return MongoUser(
            id = ObjectId(user.id),
            name = user.name,
            email = user.email,
            phoneNumber = user.phoneNumber,
            userPassword = user.userPassword,
            roles = user.roles,
            devices = mutableListOf()
        )
    }

    fun createRole(): Role {
        return Role(id = ObjectId().toString(), roleName = Role.RoleName.USER)
    }
}
