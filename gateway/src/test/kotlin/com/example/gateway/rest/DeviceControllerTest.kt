package com.example.gateway.rest

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.getDeviceByIdRequest
import DeviceProtoFixture.successfulCreateResponse
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import com.example.core.dto.response.DeviceResponseDto
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.CreateDeviceMapper
import com.example.gateway.mapper.DeleteDeviceMapper
import com.example.gateway.mapper.GetAllDevicesMapper
import com.example.gateway.mapper.GetDeviceByIdMapper
import com.example.gateway.mapper.UpdateDeviceMapper
import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.kotlin.core.publisher.toMono
import kotlin.test.Test

@WebFluxTest(DeviceController::class)
class DeviceControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var natsClient: NatsClient

    @MockkBean
    private lateinit var createDeviceMapper: CreateDeviceMapper

    @MockkBean
    private lateinit var getDeviceByIdMapper: GetDeviceByIdMapper

    @MockkBean
    private lateinit var updateDeviceMapper: UpdateDeviceMapper

    @MockkBean
    private lateinit var getAllDevicesMapper: GetAllDevicesMapper

    @MockkBean
    private lateinit var deleteDeviceMapper: DeleteDeviceMapper


    @Test
    fun `get by id should return device by id`() {
        // GIVEN
        val deviceId = ObjectId().toString()
        val response = successfulGetDeviceByIdResponse(deviceProto)

        every {
            natsClient.request(
                GET_BY_ID, getDeviceByIdRequest(deviceId), GetDeviceByIdResponse.parser()
            )
        } returns response.toMono()

        every { getDeviceByIdMapper.toDto(response) } returns deviceResponseDto

        // WHEN // THEN
        webTestClient.get()
            .uri("$URL/$deviceId")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<DeviceResponseDto>().isEqualTo(deviceResponseDto)
    }

//    @Test
//    fun `creating device should create a new device`() {
//        val response = successfulCreateResponse(deviceProto)
//        // GIVEN
//        every {
//            natsClient.request(
//                CREATE, passDto.toCreatePassRequest(), CreateDeviceResponse.parser()
//            )
//        } returns response.toMono()
//
//        every { createDeviceMapper.toDto(response) } returns deviceResponseDto
//
//        // WHEN // THEN
//        webTestClient.post().uri(URL).contentType(MediaType.APPLICATION_JSON).bodyValue(deviceResponseDto).exchange()
//            .expectStatus().isCreated.expectBody<DeviceResponseDto>().isEqualTo(deviceResponseDto)
//    }

    private companion object {
        const val URL = "/devices"
    }

}