package com.example.gateway.grpc

import com.example.gateway.mapper.grpc.CreateDeviceGrpcMapper
import com.example.gateway.mapper.grpc.GetDeviceByIdGrpcMapper
import com.example.gateway.mapper.grpc.GetUpdatedDeviceGrpcMapper
import com.example.grpcapi.reqrep.device.GetUpdatedDeviceRequest
import com.example.grpcapi.reqrep.device.StreamUpdatedDeviceResponse
import com.example.grpcapi.service.ReactorDeviceServiceGrpc
import com.example.internal.NatsSubject
import com.example.internal.NatsSubject.Device.CREATE
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.NatsSubject.Device.GET_BY_USER_ID
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import com.example.grpcapi.reqrep.device.CreateDeviceRequest as GrpcCreateDeviceRequest
import com.example.grpcapi.reqrep.device.CreateDeviceResponse as GrpcCreateDeviceResponse
import com.example.grpcapi.reqrep.device.GetDeviceByIdRequest as GrpcGetDeviceByIdRequest
import com.example.grpcapi.reqrep.device.GetDeviceByIdResponse as GrpcGetDeviceByIdResponse

@GrpcService
class GrpcDeviceService(
    private val getDeviceByIdGrpcMapper: GetDeviceByIdGrpcMapper,
    private val natsMessagePublisher: NatsMessagePublisher,
    private val natsHandlerManager: NatsHandlerManager,
    private val createDeviceGrpcMapper: CreateDeviceGrpcMapper,
    private val getUpdatedDeviceGrpcMapper: GetUpdatedDeviceGrpcMapper,
) : ReactorDeviceServiceGrpc.DeviceServiceImplBase() {
    override fun subscribeToUpdateByUserId(request: Mono<GetUpdatedDeviceRequest>): Flux<StreamUpdatedDeviceResponse> {
        return request.flatMapMany { updateDeviceRequest ->
            val existingDevices = natsMessagePublisher.request(
                GET_BY_USER_ID,
                GetDevicesByUserIdRequest.newBuilder().apply {
                    userId = updateDeviceRequest.userId
                }.build(),
                GetDevicesByUserIdResponse.parser()
            ).flatMapMany { response ->
                getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(response).toFlux()
            }

            natsHandlerManager.subscribe(NatsSubject.Device.updateByUserId(updateDeviceRequest.userId)) { message ->
                DeviceUpdatedEvent.parser().parseFrom(message.data)
            }
                .map { getUpdatedDeviceGrpcMapper.toUpdatedDeviceResponse(it) }
                .startWith(existingDevices)
                .takeUntil { updatedDeviceResponse ->
                    updatedDeviceResponse.hasFailure()
                }
        }
    }

    override fun getDeviceById(request: Mono<GrpcGetDeviceByIdRequest>): Mono<GrpcGetDeviceByIdResponse> {
        return request
            .map { getDeviceByIdGrpcMapper.toInternal(it) }
            .flatMap { natsMessagePublisher.request(GET_BY_ID, it, GetDeviceByIdResponse.parser()) }
            .map { getDeviceByIdGrpcMapper.toGrpc(it) }
    }

    override fun createDevice(request: Mono<GrpcCreateDeviceRequest>): Mono<GrpcCreateDeviceResponse> {
        return request
            .map { createDeviceGrpcMapper.toInternal(it) }
            .flatMap {
                natsMessagePublisher.request(
                    CREATE,
                    it,
                    CreateDeviceResponse.parser()
                )
            }
            .map { createDeviceGrpcMapper.toGrpc(it) }
    }
}
