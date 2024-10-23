package com.example.iotmanagementdevice.controller

import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

abstract class AbstractNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var natsConnection: Connection

    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10)
        )
        return parser.parseFrom(response.get().data)
    }
}