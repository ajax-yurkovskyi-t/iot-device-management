package com.example.gateway.infrastructure.rest

import com.example.gateway.infrastructure.rest.dto.request.DeviceCreateRequestDto
import com.example.gateway.infrastructure.rest.dto.request.DeviceUpdateRequestDto
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
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val createDeviceMapper: CreateDeviceMapper,
    private val getDeviceByIdMapper: GetDeviceByIdMapper,
    private val updateDeviceMapper: UpdateDeviceMapper,
    private val getAllDevicesMapper: GetAllDevicesMapper,
    private val deleteDeviceMapper: DeleteDeviceMapper,
    private val natsMessagePublisher: NatsMessagePublisher,
) {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getDeviceById(@PathVariable(name = "id") id: String): Mono<DeviceResponseDto> {
        return natsMessagePublisher.request(
            GET_BY_ID,
            GetDeviceByIdRequest.newBuilder().setId(id).build(),
            GetDeviceByIdResponse.parser()
        )
            .map { getDeviceByIdMapper.toDto(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody requestDto: DeviceCreateRequestDto): Mono<DeviceResponseDto> {
        val payload = createDeviceMapper.toCreateRequestProto(requestDto)
        return natsMessagePublisher.request(CREATE, payload, CreateDeviceResponse.parser())
            .map { createDeviceMapper.toDto(it) }
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody requestDto: DeviceUpdateRequestDto
    ): Mono<DeviceResponseDto> {
        val payload = updateDeviceMapper.toUpdateRequestProto(requestDto, id)
        return natsMessagePublisher.request(UPDATE, payload, UpdateDeviceResponse.parser())
            .map { updateDeviceMapper.toDto(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> {
        return natsMessagePublisher.request(
            DELETE,
            DeleteDeviceRequest.newBuilder().setId(id).build(),
            DeleteDeviceResponse.parser()
        )
            .map { deleteDeviceMapper.toDeleteResponse(it) }
    }

    @GetMapping
    fun getAll(): Mono<List<DeviceResponseDto>> {
        return natsMessagePublisher.request(
            GET_ALL,
            GetAllDevicesRequest.newBuilder().build(),
            GetAllDevicesResponse.parser()
        )
            .map { getAllDevicesMapper.toDto(it) }
    }
}
