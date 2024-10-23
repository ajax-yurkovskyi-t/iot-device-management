package com.example.iotmanagementdevice.beanpostprocessor

import com.example.iotmanagementdevice.controller.nats.NatsController
import com.google.protobuf.GeneratedMessage
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import io.nats.client.Subscription
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsControllerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        if (bean is NatsController<*, *>) {
            dispatch(bean)
        }
        return bean
    }

    fun <T : GeneratedMessage, R : GeneratedMessage> dispatch(controller: NatsController<T, R>): Subscription {
        val messageHandler = MessageHandler {
            Mono.fromCallable { controller.parser }
                .map { parser -> parser.parseFrom(it.data) }
                .flatMap { parsedData -> controller.handle(parsedData) }
                .subscribe { response ->
                    controller.connection.publish(it.replyTo, response.toByteArray())
                }
        }
        return dispatcher.subscribe(controller.subject, controller.queueGroup, messageHandler)
    }
}
