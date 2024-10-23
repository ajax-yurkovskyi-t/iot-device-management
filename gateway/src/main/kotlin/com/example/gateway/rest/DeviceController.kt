package com.example.gateway.rest

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.GetDeviceByIdMapper
import com.example.gateway.mapper.UpdateDeviceMapper
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import com.example.internal.NatsSubject.Device.CREATE;
import com.example.internal.NatsSubject.Device.GET_BY_ID
import com.example.internal.NatsSubject.Device.UPDATE
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.gateway.mapper.CreateDeviceMapper
import com.example.gateway.mapper.DeleteDeviceMapper
import com.example.gateway.mapper.GetAllDevicesMapper
import com.example.internal.NatsSubject.Device.DELETE
import com.example.internal.NatsSubject.Device.GET_ALL
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
@RequestMapping("/devices")
class DeviceController(
    private val createDeviceMapper: CreateDeviceMapper,
    private val getDeviceByIdMapper: GetDeviceByIdMapper,
    private val updateDeviceMapper: UpdateDeviceMapper,
    private val getAllDevicesMapper: GetAllDevicesMapper,
    private val deleteDeviceMapper: DeleteDeviceMapper,
    private val natsClient: NatsClient,
) {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getDeviceById(@PathVariable(name = "id") id: String): Mono<DeviceResponseDto> {
        return natsClient.request(GET_BY_ID,
            GetDeviceByIdRequest.newBuilder().setId(id).build(),
            GetDeviceByIdResponse.parser()
        )
            .map { getDeviceByIdMapper.toDto(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody requestDto: DeviceCreateRequestDto): Mono<DeviceResponseDto> {
        val payload = createDeviceMapper.toCreateRequestProto(requestDto)
        return natsClient.request(CREATE, payload, CreateDeviceResponse.parser())
            .map { createDeviceMapper.toDto(it) }
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody requestDto: DeviceUpdateRequestDto
    ): Mono<DeviceResponseDto> {
        val payload = updateDeviceMapper.toUpdateRequestProto(requestDto, id)
        return natsClient.request(UPDATE, payload, UpdateDeviceResponse.parser())
            .map { updateDeviceMapper.toDto(it) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> {
        return natsClient.request(
            DELETE,
            DeleteDeviceRequest.newBuilder().setId(id).build(),
            DeleteDeviceResponse.parser()
        )
            .map { deleteDeviceMapper.toDeleteResponse(it) }
    }

    @GetMapping
    fun getAll(): Mono<List<DeviceResponseDto>> {
        return natsClient.request(
            GET_ALL,
            GetAllDevicesRequest.newBuilder().build(),
            GetAllDevicesResponse.parser()
        )
            .map { getAllDevicesMapper.toDto(it) }
    }
}
