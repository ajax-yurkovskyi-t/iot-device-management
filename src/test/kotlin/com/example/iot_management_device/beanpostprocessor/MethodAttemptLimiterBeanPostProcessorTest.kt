package com.example.iot_management_device.beanpostprocessor

import com.example.iot_management_device.exception.AttemptLimitReachedException
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetails
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class MethodAttemptLimiterBeanPostProcessorTest {

    @SpyK
    private var processor = MethodAttemptLimiterBeanPostProcessor()

    @MockK
    private lateinit var userMock: UserDetails

    @MockK
    private lateinit var contextMock: SecurityContext

    @MockK
    private lateinit var authenticationMock: Authentication

    @MockK
    private lateinit var webAuthDetailsMock: WebAuthenticationDetails

    @BeforeEach
    fun setUp() {
        every { authenticationMock.principal } returns userMock
        every { userMock.username } returns "test@example.com"
        every { authenticationMock.details } returns webAuthDetailsMock
        every { webAuthDetailsMock.remoteAddress } returns "127.0.0.1"
        every { contextMock.authentication } returns authenticationMock

        SecurityContextHolder.setContext(contextMock)
    }

    @Test
    fun `should return proxy of bean with MethodAttemptLimiter annotation`() {
        // Given
        val annotatedBean = SampleService()
        val beanName = "sampleBeanName"

        // When
        processor.postProcessBeforeInitialization(annotatedBean, beanName)
        val proxy = processor.postProcessAfterInitialization(annotatedBean, beanName)

        // Then
        assertTrue(
            Proxy.isProxyClass(proxy?.javaClass),
            "Bean post processor should return proxy instead of original bean"
        )
    }

    @Test
    fun `should throw AttemptLimitReachedException after exceeding limit`() {
        // Given
        val service = MultiMethodService()
        val beanName = "multiMethodService"
        processor.postProcessBeforeInitialization(service, beanName)
        val proxy = processor.postProcessAfterInitialization(service, beanName) as MultiMethodServiceInterface

        // When
        repeat(3) { proxy.firstMethod() } // Within the limit

        // Then
        val exception = assertThrows<AttemptLimitReachedException> {
            proxy.firstMethod() // Exceed the limit
        }
        assertEquals(
            "Too many attempts for method 'firstMethod'. Please try again after 60 seconds.",
            exception.message
        )
    }

    @Test
    fun `should throw IllegalArgumentException for negative maxAttempts`() {
        // Given
        val service = NegativeAnnotationService()
        val beanName = "negativeAnnotationService"

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            processor.postProcessBeforeInitialization(service, beanName)
        }
        assertEquals("maxAttempts cannot be negative: -1", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException for negative lockoutDurationMillis`() {
        // Given
        val service = NegativeAnnotationService2()
        val beanName = "negativeAnnotationService2"

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            processor.postProcessBeforeInitialization(service, beanName)
        }
        assertEquals("lockoutDurationMillis cannot be negative: -500", exception.message)
    }

    @Test
    fun `should independently track attempts for multiple methods`() {
        // Given
        val service = MultiMethodService()
        val beanName = "multiMethodService"
        processor.postProcessBeforeInitialization(service, beanName)
        val proxy = processor.postProcessAfterInitialization(service, beanName) as MultiMethodServiceInterface

        // When
        repeat(3) { proxy.firstMethod() }  // Within limit for firstMethod

        // Then
        assertThrows<AttemptLimitReachedException> {
            proxy.firstMethod()  // Exceed limit for firstMethod
        }

        assertThrows<AttemptLimitReachedException> {
            proxy.secondMethod()
        }
    }

    @Test
    fun `should enforce and reset lockout duration correctly`() {
        // Given
        val service = SampleService()
        val beanName = "sampleService"
        processor.postProcessBeforeInitialization(service, beanName)
        val proxy = processor.postProcessAfterInitialization(service, beanName) as SampleServiceInterface

        // When
        repeat(3) { proxy.limitedMethod() }  // Within the limit

        // Then
        assertThrows<AttemptLimitReachedException> {
            proxy.limitedMethod()  // Exceed the limit
        }

        // Sleep for 0.5 second to allow lockout to reset
        Thread.sleep(500)

        // When & Then
        assertDoesNotThrow {
            proxy.limitedMethod()
        }
    }

    class MultiMethodService : MultiMethodServiceInterface {
        @MethodAttemptLimiter(maxAttempts = 3, lockoutDurationMillis = 60000)
        override fun firstMethod() {
            // Method logic
        }

        @MethodAttemptLimiter(maxAttempts = 0, lockoutDurationMillis = 60000)
        override fun secondMethod() {
            // Method logic
        }
    }

    interface MultiMethodServiceInterface {
        fun firstMethod()
        fun secondMethod()
    }

    class SampleService : SampleServiceInterface {
        @MethodAttemptLimiter(maxAttempts = 3, lockoutDurationMillis = 500)
        override fun limitedMethod() {
            // Method logic
        }
    }

    class NegativeAnnotationService : SampleServiceInterface {
        @MethodAttemptLimiter(maxAttempts = -1, lockoutDurationMillis = 60000)
        override fun limitedMethod() {
            // Method logic
        }
    }

    class NegativeAnnotationService2 : SampleServiceInterface {
        @MethodAttemptLimiter(maxAttempts = 3, lockoutDurationMillis = -500)
        override fun limitedMethod() {
            // Method logic
        }
    }

    interface SampleServiceInterface {
        fun limitedMethod()
    }
}
