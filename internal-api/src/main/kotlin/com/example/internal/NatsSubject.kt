package com.example.internal

object NatsSubject {
    private const val REQUEST_PREFIX = "com.example.iotmanagementdevice.input.request"

    object Device {
        private const val DEVICE_PREFIX = "$REQUEST_PREFIX.device"
        const val DEVICE_QUEUE_GROUP = "deviceQueueGroup"

        const val GET_BY_ID = "$DEVICE_PREFIX.get_by_id"
        const val CREATE = "$DEVICE_PREFIX.create"
        const val GET_ALL = "$DEVICE_PREFIX.get_all"
        const val UPDATE = "$DEVICE_PREFIX.update"
        const val DELETE = "$DEVICE_PREFIX.delete"
    }
}