package com.example.iotmanagementdevice.beanpostprocessor

import com.example.iotmanagementdevice.exception.AttemptLimitReachedException
import com.example.iotmanagementdevice.model.MongoUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Component
class MethodAttemptLimiterBeanPostProcessor : BeanPostProcessor {
    private val beanMap = HashMap<String, Any>()
    private val attemptMap = ConcurrentHashMap<String, MethodAttempt>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean.javaClass
        beanClass.methods.forEach { method ->
            if (method.isAnnotationPresent(MethodAttemptLimiter::class.java)) {
                validateAnnotationFields(method.getAnnotation(MethodAttemptLimiter::class.java))
                beanMap[beanName] = bean
                log.info("Method ${method.name} in Bean $beanName is annotated with @MethodAttemptLimiter")
            }
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val originalBean = beanMap[beanName] ?: return bean
        val originalBeanClass = originalBean.javaClass

        return Proxy.newProxyInstance(
            originalBeanClass.classLoader,
            originalBeanClass.interfaces,
        ) { _, method, args ->

            val annotation =
                findAnnotationInClass(originalBeanClass, method)
            val key = generateAttemptKey(beanName, method)
            val attempt = attemptMap.getOrPut(key) {
                annotation?.run {
                    MethodAttempt(
                        maxAttempts,
                        lockoutDurationMillis
                    )
                } ?: error("Missing limiting annotation for method: ${method.name}")
            }
            val methodName = method.name
            if (attempt.isLockedOut()) {
                val remainingSeconds = attempt.getRemainingLockoutTime()
                throw AttemptLimitReachedException(ATTEMPT_LIMIT_MESSAGE.format(methodName, remainingSeconds))
            }
            attempt.incrementAttempts()
            if (attempt.hasExceededLimit()) {
                attempt.lockOut()
                throw AttemptLimitReachedException(
                    ATTEMPT_LIMIT_MESSAGE.format(
                        methodName,
                        TimeUnit.MILLISECONDS.toSeconds(attempt.lockoutDurationMillis)
                    )
                )
            }
            @Suppress("SpreadOperator")
            method.invoke(originalBean, *(args ?: emptyArray()))
        }
    }

    private fun validateAnnotationFields(annotation: MethodAttemptLimiter) {
        require(annotation.maxAttempts >= 0) {
            "maxAttempts cannot be negative: ${annotation.maxAttempts}"
        }
        require(annotation.lockoutDurationMillis >= 0) {
            "lockoutDurationMillis cannot be negative: ${annotation.lockoutDurationMillis}"
        }
    }

    private fun findAnnotationInClass(beanClass: Class<*>, method: Method) =
        beanClass.methods.find { it.name == method.name && it.parameterTypes.contentEquals(method.parameterTypes) }
            ?.getAnnotation(MethodAttemptLimiter::class.java)

    private fun generateAttemptKey(beanName: String, method: Method): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val identifier = (authentication?.principal as? MongoUser)?.email ?: getClientIP()

        return "${beanName}_${method.name}_$identifier"
    }

    private fun getClientIP(): String =
        (SecurityContextHolder.getContext().authentication.details as WebAuthenticationDetails).remoteAddress

    companion object {
        private val log = LoggerFactory.getLogger(MethodAttemptLimiterBeanPostProcessor::class.java)
        private const val ATTEMPT_LIMIT_MESSAGE =
            "Too many attempts for method '%s'. Please try again after %d seconds."
    }
}
