package com.example.gateway.infrastructure.grpc

import com.example.gateway.application.port.input.DeviceInputPort
import com.example.gateway.infrastructure.grpc.mapper.CreateDeviceGrpcMapper
import com.example.gateway.infrastructure.grpc.mapper.GetDeviceByIdGrpcMapper
import com.example.gateway.infrastructure.grpc.mapper.GetUpdatedDeviceGrpcMapper
import com.example.grpcapi.reqrep.device.CreateDeviceRequest
import com.example.grpcapi.reqrep.device.CreateDeviceResponse
import com.example.grpcapi.reqrep.device.GetDeviceByIdRequest
import com.example.grpcapi.reqrep.device.GetDeviceByIdResponse
import com.example.grpcapi.reqrep.device.GetUpdatedDeviceRequest
import com.example.grpcapi.reqrep.device.StreamUpdatedDeviceResponse
import com.example.grpcapi.service.ReactorDeviceServiceGrpc
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@GrpcService
class GrpcDeviceService(
    private val deviceInputPort: DeviceInputPort,
    private val getDeviceByIdGrpcMapper: GetDeviceByIdGrpcMapper,
    private val createDeviceGrpcMapper: CreateDeviceGrpcMapper,
    private val getUpdatedDeviceGrpcMapper: GetUpdatedDeviceGrpcMapper,
) : ReactorDeviceServiceGrpc.DeviceServiceImplBase() {
    override fun subscribeToUpdateByUserId(request: Mono<GetUpdatedDeviceRequest>): Flux<StreamUpdatedDeviceResponse> {
        return request.flatMapMany { updateDeviceRequest ->
            val getDevicesRequest = GetDevicesByUserIdRequest.newBuilder().apply {
                userId = updateDeviceRequest.userId
            }.build()

            val existingDevices = deviceInputPort.getDevicesByUserId(getDevicesRequest)
                .flatMapMany { response ->
                    getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(response).toFlux()
                }

            deviceInputPort.subscribeToUpdatesByUserId(updateDeviceRequest.userId)
                .map { getUpdatedDeviceGrpcMapper.toUpdatedDeviceResponse(it) }
                .startWith(existingDevices)
                .takeUntil { updatedDeviceResponse ->
                    updatedDeviceResponse.hasFailure()
                }
        }
    }

    override fun createDevice(request: Mono<CreateDeviceRequest>): Mono<CreateDeviceResponse> {
        return request
            .map { createDeviceGrpcMapper.toInternal(it) }
            .flatMap { deviceInputPort.createDevice(it) }
            .map { createDeviceGrpcMapper.toGrpc(it) }
    }

    override fun getDeviceById(request: Mono<GetDeviceByIdRequest>): Mono<GetDeviceByIdResponse> {
        return request
            .map { getDeviceByIdGrpcMapper.toInternal(it) }
            .flatMap { deviceInputPort.getDeviceById(it) }
            .map { getDeviceByIdGrpcMapper.toGrpc(it) }
    }
}
