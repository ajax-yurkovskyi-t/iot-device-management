package com.example.gateway.client

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsClient(private val natsConnection: Connection) {

    fun <T : GeneratedMessage, R : GeneratedMessage> request(
        subject: String,
        payload: T,
        parser: Parser<R>,
    ): Mono<R> {
        return Mono.fromFuture { natsConnection.request(subject, payload.toByteArray()) }
            .map { response -> parser.parseFrom(response.data) }
    }
}
