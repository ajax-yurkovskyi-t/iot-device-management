package com.example.iot_management_device.beanpostprocessor

import com.example.iot_management_device.exception.AccessAttemptException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@Component
class MethodAttemptLimiterBeanPostProcessor : BeanPostProcessor {
    private val beanMap = HashMap<String, Any>()
    private val attemptMap = HashMap<String, MethodAttempt>()

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
            val annotation = findAnnotationInClass(originalBeanClass, method)
            val user = SecurityContextHolder.getContext().authentication?.name
            val key = "${beanName}_${method.name}_$user"
            val attempt = attemptMap.getOrPut(key) {
                annotation?.run {
                    MethodAttempt(
                        maxAttempts,
                        lockoutDuration
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
                throw AccessAttemptException(ATTEMPT_LIMIT_MESSAGE.format(methodName, attempt.lockoutDuration / 1000))
            }

            method.invoke(originalBean, *(args ?: emptyArray()))
        }
    }

    private fun findAnnotationInClass(beanClass: Class<*>, method: Method): MethodAttemptLimiter? =
        beanClass.methods.find { it.name == method.name && it.parameterTypes.contentEquals(method.parameterTypes) }
            ?.getAnnotation(MethodAttemptLimiter::class.java)

    companion object {
        private val log = LoggerFactory.getLogger(MethodAttemptLimiterBeanPostProcessor::class.java)
        private const val ATTEMPT_LIMIT_MESSAGE =
            "Too many attempts for method '%s'. Please try again after %d seconds."
    }
}
