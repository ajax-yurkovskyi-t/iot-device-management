package com.example.gateway.grpc

import DeviceProtoFixture.createDeviceRequest
import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.getDeviceByIdRequest
import DeviceProtoFixture.getDevicesByUserIdRequest
import DeviceProtoFixture.getUpdatedDevicesRequest
import DeviceProtoFixture.grpcCreateDeviceRequest
import DeviceProtoFixture.grpcGetDeviceByIdRequest
import DeviceProtoFixture.successfulCreateResponse
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import DeviceProtoFixture.successfulGetDevicesByUserIdResponse
import DeviceProtoFixture.successfulGrpcCreateResponse
import DeviceProtoFixture.successfulGrpcGetDeviceByIdResponse
import DeviceProtoFixture.successfulUpdateResponse
import DeviceProtoFixture.updateDeviceResponseList
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.GetDevicesByUserIdMapper
import com.example.gateway.mapper.grpc.CreateDeviceGrpcMapper
import com.example.gateway.mapper.grpc.GetDeviceByIdGrpcMapper
import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class GrpcDeviceServiceTest {
    @MockK
    private lateinit var natsClient: NatsClient

    @MockK
    private lateinit var createDeviceGrpcMapper: CreateDeviceGrpcMapper

    @MockK
    private lateinit var getDeviceByIdGrpcMapper: GetDeviceByIdGrpcMapper

    @MockK
    private lateinit var getDevicesByUserIdMapper: GetDevicesByUserIdMapper

    @InjectMockKs
    private lateinit var grpcDeviceService: GrpcDeviceService

    @Test
    fun `should create a device and return grpc create response`() {
        // GIVEN
        val grpcCreateDeviceRequest = grpcCreateDeviceRequest()
        val createDeviceRequest = createDeviceRequest()
        val grpcCreateDeviceResponse = successfulGrpcCreateResponse(deviceProto)
        val createDeviceResponse = successfulCreateResponse(deviceProto)

        every { createDeviceGrpcMapper.toInternal(grpcCreateDeviceRequest) } returns createDeviceRequest
        every {
            natsClient.request(
                CREATE,
                createDeviceRequest,
                CreateDeviceResponse.parser()
            )
        } returns createDeviceResponse.toMono()
        every { createDeviceGrpcMapper.toGrpc(createDeviceResponse) } returns grpcCreateDeviceResponse

        // WHEN
        val result = grpcDeviceService.createDevice(grpcCreateDeviceRequest)

        // THEN
        result.test()
            .expectNext(grpcCreateDeviceResponse)
            .verifyComplete()
    }

    @Test
    fun `should get a device by id and return grpc get response`() {
        // GIVEN
        val deviceId = ObjectId().toString()
        val grpcGetDeviceRequest = grpcGetDeviceByIdRequest(deviceId)
        val getDeviceRequest = successfulGetDeviceByIdResponse(deviceProto)
        val grpcGetDeviceResponse = successfulGrpcGetDeviceByIdResponse(deviceProto)

        every { getDeviceByIdGrpcMapper.toInternal(grpcGetDeviceRequest) } returns getDeviceByIdRequest(
            grpcGetDeviceRequest.id
        )
        every {
            natsClient.request(
                GET_BY_ID,
                getDeviceByIdRequest(grpcGetDeviceRequest.id),
                GetDeviceByIdResponse.parser()
            )
        } returns getDeviceRequest.toMono()
        every { getDeviceByIdGrpcMapper.toGrpc(getDeviceRequest) } returns grpcGetDeviceResponse

        // WHEN
        val result = grpcDeviceService.getDeviceById(grpcGetDeviceRequest)

        // THEN
        result.test()
            .expectNext(grpcGetDeviceResponse)
            .verifyComplete()
    }

    @Test
    fun `should subscribe to device updates and return the list of updated devices`() {
        // GIVEN
        val userId = ObjectId().toString()
        val devices = listOf(deviceProto)
        val existingDevices =
            successfulGetDevicesByUserIdResponse(devices)
        val updatedDevices =
            listOf(successfulUpdateResponse(deviceProto))
        val grpcGetUpdatedDevicesRequest = getUpdatedDevicesRequest(userId)
        val grpcGetDevicesByUserIdRequest = getDevicesByUserIdRequest(userId)

        every {
            natsClient.request(
                GET_BY_USER_ID,
                grpcGetDevicesByUserIdRequest,
                GetDevicesByUserIdResponse.parser()
            )
        } returns existingDevices.toMono()

        every { getDevicesByUserIdMapper.toUpdateDeviceResponseList(existingDevices) } returns updatedDevices
        every { natsClient.subscribeByUserId(grpcGetUpdatedDevicesRequest.userId) } returns updatedDevices.toFlux()

        // WHEN
        val result = grpcDeviceService.subscribeToUpdateByUserId(grpcGetUpdatedDevicesRequest).collectList()

        // THEN
        result.test()
            .assertNext {
                assertTrue(
                    it.containsAll(updatedDevices + updateDeviceResponseList(devices)),
                    "The updated device list should contain all expected updated devices"
                )
            }
            .verifyComplete()
    }
}
