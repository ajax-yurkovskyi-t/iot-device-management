package com.example.gateway.infrastructure.rest

import DeviceProtoFixture.deviceCreateRequestDto
import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.deviceUpdateRequestDto
import DeviceProtoFixture.getDeviceByIdRequest
import DeviceProtoFixture.successfulCreateResponse
import DeviceProtoFixture.successfulGetAllDevicesResponse
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import DeviceProtoFixture.successfulUpdateResponse
import com.example.gateway.infrastructure.rest.dto.response.DeviceResponseDto
import com.example.gateway.infrastructure.rest.mapper.CreateDeviceMapper
import com.example.gateway.infrastructure.rest.mapper.DeleteDeviceMapper
import com.example.gateway.infrastructure.rest.mapper.GetAllDevicesMapper
import com.example.gateway.infrastructure.rest.mapper.GetDeviceByIdMapper
import com.example.gateway.infrastructure.rest.mapper.UpdateDeviceMapper
import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.NatsSubject.Device.DELETE
import com.example.internal.NatsSubject.Device.GET_ALL
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.NatsSubject.Device.UPDATE
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import kotlin.test.Test

@WebFluxTest(DeviceController::class)
class DeviceControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var natsMessagePublisher: NatsMessagePublisher

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
            natsMessagePublisher.request(
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

    @Test
    fun `creating device should create a new device`() {
        val response = successfulCreateResponse(deviceProto)
        // GIVEN
        every {
            natsMessagePublisher.request(
                CREATE, createDeviceMapper.toCreateRequestProto(deviceCreateRequestDto), CreateDeviceResponse.parser()
            )
        } returns response.toMono()

        every { createDeviceMapper.toDto(response) } returns deviceResponseDto

        // WHEN // THEN
        webTestClient.post()
            .uri(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(deviceResponseDto)
            .exchange()
            .expectStatus().isCreated.expectBody<DeviceResponseDto>()
            .isEqualTo(deviceResponseDto)
    }

    @Test
    fun `update should update an existing device`() {
        // GIVEN
        val deviceId = ObjectId().toString()
        val response = successfulUpdateResponse(deviceProto)

        every {
            natsMessagePublisher.request(
                UPDATE,
                updateDeviceMapper.toUpdateRequestProto(deviceUpdateRequestDto, deviceId),
                UpdateDeviceResponse.parser()
            )
        } returns response.toMono()

        every { updateDeviceMapper.toDto(response) } returns deviceResponseDto

        // WHEN // THEN
        webTestClient.put()
            .uri("$URL/$deviceId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(deviceUpdateRequestDto)
            .exchange()
            .expectStatus().isOk
            .expectBody<DeviceResponseDto>().isEqualTo(deviceResponseDto)
    }

    @Test
    fun `getAll should return all devices`() {
        // GIVEN
        val response = successfulGetAllDevicesResponse(listOf(deviceProto))

        every {
            natsMessagePublisher.request(
                GET_ALL, GetAllDevicesRequest.getDefaultInstance(),
                GetAllDevicesResponse.parser()
            )
        } returns response.toMono()

        every { getAllDevicesMapper.toDto(response) } returns listOf(deviceResponseDto)

        // WHEN // THEN
        webTestClient.get()
            .uri(URL)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody<List<DeviceResponseDto>>().isEqualTo(listOf(deviceResponseDto))
    }

    @Test
    fun `deleteById should delete a device by id`() {
        // GIVEN
        val deviceId = ObjectId().toString()
        val deleteResponse = DeleteDeviceResponse.getDefaultInstance()

        every {
            natsMessagePublisher.request(
                DELETE, DeleteDeviceRequest.newBuilder().setId(deviceId).build(), DeleteDeviceResponse.parser()
            )
        } returns deleteResponse.toMono()

        every { deleteDeviceMapper.toDeleteResponse(deleteResponse) } returns Unit

        // WHEN // THEN
        webTestClient.delete()
            .uri("$URL/$deviceId")
            .exchange()
            .expectStatus().isNoContent
    }

    private companion object {
        const val URL = "/devices"
    }
}
