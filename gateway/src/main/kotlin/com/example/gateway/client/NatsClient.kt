package com.example.gateway.client

import com.example.internal.NatsSubject
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class NatsClient(
    private val natsConnection: Connection,
    private val dispatcher: Dispatcher,
) {
    fun <T : GeneratedMessage, R : GeneratedMessage> request(
        subject: String,
        payload: T,
        parser: Parser<R>,
    ): Mono<R> {
        return Mono.fromFuture { natsConnection.request(subject, payload.toByteArray()) }
            .map { response -> parser.parseFrom(response.data) }
    }

    fun subscribeToDeviceUpdatesByUserId(userId: String): Flux<DeviceUpdatedEvent> {
        val subjectName = NatsSubject.Device.updateByUserId(userId)
        return Flux.create { fluxSink ->
            val subscription = dispatcher.subscribe(subjectName) { message ->
                val updatedDeviceMessage = DeviceUpdatedEvent.parser().parseFrom(message.data)
                fluxSink.next(updatedDeviceMessage)
            }
            fluxSink.onDispose {
                log.info("Unsubscribe from Nats subject {}", subjectName)
                dispatcher.unsubscribe(subscription)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NatsClient::class.java)
    }
}
