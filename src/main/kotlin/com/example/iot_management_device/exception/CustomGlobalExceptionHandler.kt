package com.example.iot_management_device.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class CustomGlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val body = LinkedHashMap<String, Any>().apply {
            put("timestamp", LocalDateTime.now())
            put("status", HttpStatus.BAD_REQUEST)
            put("errors", ex.bindingResult.allErrors.map { getErrorMessage(it) })
        }
        return ResponseEntity(body, headers, status)
    }

    private fun getErrorMessage(e: ObjectError): String {
        return if (e is FieldError) {
            "${e.field} ${e.defaultMessage}"
        } else {
            e.defaultMessage ?: "Unknown error"
        }
    }

    @ExceptionHandler(RegistrationException::class)
    fun handleRegistrationException(ex: RegistrationException): ResponseEntity<Any> {
        return handleException(HttpStatus.BAD_REQUEST, ex)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<Any> {
        return handleException(HttpStatus.FORBIDDEN, ex)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Any> {
        return handleException(HttpStatus.FORBIDDEN, ex)
    }

    private fun handleException(status: HttpStatus, ex: Exception): ResponseEntity<Any> {
        val body = LinkedHashMap<String, Any>().apply {
            put("timestamp", LocalDateTime.now())
            put("status", status)
            put("message", ex.message ?: "No message available")
        }
        return ResponseEntity(body, status)
    }
}
