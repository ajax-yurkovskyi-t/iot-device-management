package com.example.iot_management_device.service.user

import com.example.iot_management_device.model.User
import com.example.iot_management_device.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun register(user:User): User {
       return userRepository.save(user)
    }

    override fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { RuntimeException() }
    }

    override fun getAll(): List<User> {
       return userRepository.findAll()
    }

    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
    }

    override fun update(id: Long?, user: User): User {
        return userRepository.save(user)
    }

}