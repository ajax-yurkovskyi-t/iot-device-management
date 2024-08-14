package com.example.iot_management_device.controller


import com.example.iot_management_device.model.User
import com.example.iot_management_device.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getUserById(@PathVariable(name = "id") id:Long): User {
        return userService.getUserById(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody user: User): User =
        userService.register(user)


    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    fun update(
       @Valid @RequestBody requestDto: User
    ): User {
        return userService.update(requestDto.id ,requestDto);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getUserByUsername(
        @RequestParam username: String
    ): User {
        return userService.getUserByUsername(username)
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getAll() : List<User> {
        return userService.getAll();
    }
}
