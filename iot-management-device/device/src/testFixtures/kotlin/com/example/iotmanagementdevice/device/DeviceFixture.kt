package com.example.iotmanagementdevice.device

import com.example.commonmodels.device.Device
import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceRequest
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesRequest
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdRequest
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.infrastructure.mongo.entity.MongoDevice
import com.google.protobuf.Timestamp
import org.bson.types.ObjectId
import com.example.iotmanagementdevice.device.domain.Device as DomainDevice

object DeviceFixture {
    fun createDevice(): DomainDevice {
        return DomainDevice(
            id = ObjectId().toString(),
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DomainDevice.DeviceStatusType.ONLINE,
            userId = ObjectId().toString(),
            updatedAt = null
        )
    }

    fun createDeviceCreate(): CreateDevice {
        return CreateDevice(
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DomainDevice.DeviceStatusType.ONLINE,
        )
    }

    fun createMongoDevice(device: DomainDevice): MongoDevice {
        return MongoDevice(
            id = ObjectId(device.id),
            name = device.name,
            description = device.description,
            type = device.type,
            statusType = MongoDevice.DeviceStatusType.ONLINE,
            userId = ObjectId(device.userId),
            updatedAt = device.updatedAt
        )
    }

    fun createDeviceWithoutId(): DomainDevice {
        return DomainDevice(
            id = ObjectId().toString(),
            name = "Device1",
            description = "A test device",
            type = "Sensor",
            statusType = DomainDevice.DeviceStatusType.ONLINE,
            userId = null,
            updatedAt = null
        )
    }

    fun createDeviceRequest(): CreateDeviceRequest = CreateDeviceRequest.newBuilder().apply {
        setName("ProtoDevice")
        setType("Sensor")
        setDescription("A test device")
        setStatusType(Device.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun deleteDeviceRequest(deviceId: String): DeleteDeviceRequest =
        DeleteDeviceRequest.newBuilder().setId(deviceId).build()

    fun getDeviceByIdRequest(deviceId: String): GetDeviceByIdRequest =
        GetDeviceByIdRequest.newBuilder().setId(deviceId).build()

    fun updateDeviceRequest(deviceId: String): UpdateDeviceRequest = UpdateDeviceRequest.newBuilder().apply {
        setId(deviceId)
        setName("ProtoDevice")
        setType("Sensor")
        setDescription("A test device")
        setStatusType(Device.StatusType.STATUS_TYPE_ONLINE)
    }.build()

    fun getAllDevicesRequest(): GetAllDevicesRequest =
        GetAllDevicesRequest.newBuilder().build()

    fun deviceUpdatedEvent(device: DomainDevice): DeviceUpdatedEvent {
        val device = Device.newBuilder().apply {
            id = device.id
            this.userId = device.userId
            name = device.name
            description = device.description
            type = device.type
            statusType = mapStatusType(device.statusType)
            updatedAt = Timestamp.newBuilder().apply {
                seconds = device.updatedAt!!.epochSecond
                nanos = device.updatedAt!!.nano
            }.build()
        }.build()

        return DeviceUpdatedEvent.newBuilder().apply {
            this.device = device
        }.build()
    }

    fun updateDeviceNotification(device: DomainDevice): DeviceUpdateNotification {
        return DeviceUpdateNotification.newBuilder().apply {
            this.deviceId = device.id
            this.userId = device.userId
            this.timestamp = Timestamp.newBuilder().apply {
                seconds = device.updatedAt!!.epochSecond
                nanos = device.updatedAt!!.nano
            }.build()
        }.build()
    }

    fun getDevicesByUserIdRequest(userId: String): GetDevicesByUserIdRequest {
        return GetDevicesByUserIdRequest.newBuilder().apply {
            this.userId = userId
        }.build()
    }

    fun getDevicesByUserIdResponse(list: List<DomainDevice>): GetDevicesByUserIdResponse {
        return GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addAllDevices(
                list.map { device ->
                    Device.newBuilder().apply {
                        id = device.id
                        name = device.name
                        type = device.type
                        description = device.description
                        statusType = mapStatusType(device.statusType)
                        userId = device.userId
                    }.build()
                }
            )
        }.build()
    }

    private fun mapStatusType(statusType: DomainDevice.DeviceStatusType): Device.StatusType {
        return when (statusType) {
            DomainDevice.DeviceStatusType.ONLINE -> Device.StatusType.STATUS_TYPE_ONLINE
            DomainDevice.DeviceStatusType.OFFLINE -> Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }
}
