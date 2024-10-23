package com.example.iotmanagementdevice.controller.nats

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import reactor.core.publisher.Mono

interface NatsController<T : GeneratedMessage, R : GeneratedMessage> {
    val connection: Connection
    val subject: String
    val queueGroup: String
    val parser: Parser<T>
    fun handle(request: T): Mono<R>
}
