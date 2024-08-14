package com.example.iot_management_device.service.user

import com.example.iot_management_device.model.User


interface UserService {
    fun register(user:User): User

    fun getUserById(id:Long): User

    fun getAll(): List<User>

    fun getUserByUsername(username:String):User

    fun update(id:Long, user:User): User

    fun deleteById(id:Long)
}