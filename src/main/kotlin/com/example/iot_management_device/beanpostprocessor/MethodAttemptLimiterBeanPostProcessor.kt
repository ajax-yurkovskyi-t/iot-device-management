package com.example.iot_management_device.beanpostprocessor

import com.example.iot_management_device.exception.AccessAttemptException
import com.example.iot_management_device.model.User
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetails
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

@Component
class MethodAttemptLimiterBeanPostProcessor : BeanPostProcessor {
    private val beanMap = HashMap<String, Any>()
    private val attemptMap = ConcurrentHashMap<String, MethodAttempt>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean.javaClass
        beanClass.methods.forEach { method ->
            if (method.isAnnotationPresent(MethodAttemptLimiter::class.java)) {
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
                } ?: throw IllegalStateException("Missing limiting annotation for method: ${method.name}")
            }
            val methodName = method.name
            if (attempt.isLockedOut()) {
                val remainingSeconds = attempt.getRemainingLockoutTime()
                throw AccessAttemptException(ATTEMPT_LIMIT_MESSAGE.format(methodName, remainingSeconds))
            }
            attempt.incrementAttempts()
            if (attempt.hasExceededLimit()) {
                attempt.lockOut()
                throw AccessAttemptException(ATTEMPT_LIMIT_MESSAGE.format(methodName, attempt.lockoutDurationMillis / 1000))
            }

            method.invoke(originalBean, *(args ?: emptyArray()))
        }
    }

    private fun findAnnotationInClass(beanClass: Class<*>, method: Method) =
        beanClass.methods.find { it.name == method.name && it.parameterTypes.contentEquals(method.parameterTypes) }
            ?.getAnnotation(MethodAttemptLimiter::class.java)

    private fun generateAttemptKey(beanName: String, method: Method): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val userEmail = (authentication?.principal as? User)?.email
        val identifier = userEmail ?: getClientIP()

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
