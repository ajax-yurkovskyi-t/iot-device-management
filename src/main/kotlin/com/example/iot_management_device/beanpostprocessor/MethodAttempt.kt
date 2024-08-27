package com.example.iot_management_device.beanpostprocessor

data class MethodAttempt(
    val maxAttempts: Int,
    val lockoutDuration: Long,
    var attempts: Int = 0,
    var lockoutEndTime: Long = 0,
) {
    fun incrementAttempts() {
        attempts++
    }

    fun hasExceededLimit() =
        attempts > maxAttempts

    fun isLockedOut() =
        lockoutEndTime > System.currentTimeMillis()

    fun lockOut() {
        lockoutEndTime = System.currentTimeMillis() + lockoutDuration
        attempts = 0
    }

    fun getRemainingLockoutTime(): Long {
        val remainingTime = lockoutEndTime - System.currentTimeMillis()
        return (remainingTime / 1000)
    }
}
