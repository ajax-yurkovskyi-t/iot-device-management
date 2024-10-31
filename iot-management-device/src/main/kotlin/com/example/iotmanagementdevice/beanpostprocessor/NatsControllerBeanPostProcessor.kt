package com.example.iotmanagementdevice.beanpostprocessor

import com.example.iotmanagementdevice.controller.nats.NatsController
import com.google.protobuf.GeneratedMessage
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import io.nats.client.Subscription
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class NatsControllerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (bean is NatsController<*, *>) {
            dispatch(bean)
        }
        return bean
    }

    private fun <T : GeneratedMessage, R : GeneratedMessage> dispatch(
        controller: NatsController<T, R>,
    ): Subscription {
        val messageHandler = MessageHandler {
            Mono.fromCallable { controller.parser }
                .map { parser -> parser.parseFrom(it.data) }
                .flatMap { parsedData -> controller.handle(parsedData) }
                .onErrorResume { throwable -> handleParseError(throwable, controller.responseType) }
                .subscribe { response ->
                    controller.connection.publish(it.replyTo, response.toByteArray())
                }
        }
        return dispatcher.subscribe(controller.subject, controller.queueGroup, messageHandler)
    }

    private fun <R : GeneratedMessage> handleParseError(throwable: Throwable, responseType: R): Mono<R> {
        val message = throwable.message.orEmpty()
        val responseBuilder = responseType.toBuilder()

        val failureDescriptor = responseType.descriptorForType.findFieldByName(FAILURE)
        val messageDescriptor = failureDescriptor.messageType.findFieldByName(MESSAGE_FIELD)
        val response = responseBuilder.run {
            val failure = newBuilderForField(failureDescriptor).setField(messageDescriptor, message).build()
            setField(failureDescriptor, failure)
        }.build()

        return (response as? R)?.toMono() ?: throwable.toMono()
    }

    companion object {
        private const val FAILURE = "failure"
        private const val MESSAGE_FIELD = "message"
    }
}
