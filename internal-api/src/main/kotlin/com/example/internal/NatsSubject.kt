package com.example.internal

object NatsSubject {
    private const val REQUEST_PREFIX = "com.example.iotmanagementdevice.input.request"
    private const val EVENT_PREFIX = "com.example.iotmanagementdevice.output.pubsub"

    object Device {
        private const val DEVICE_REQUEST_PREFIX = "$REQUEST_PREFIX.device"
        private const val DEVICE_EVENT_PREFIX = "$EVENT_PREFIX.device"

        const val GET_BY_ID = "$DEVICE_REQUEST_PREFIX.get_by_id"
        const val CREATE = "$DEVICE_REQUEST_PREFIX.create"
        const val GET_ALL = "$DEVICE_REQUEST_PREFIX.get_all"
        const val UPDATE = "$DEVICE_REQUEST_PREFIX.update"
        const val DELETE = "$DEVICE_REQUEST_PREFIX.delete"
        const val GET_BY_USER_ID = "$DEVICE_REQUEST_PREFIX.get_by_user_id"

        fun updateByUserId(userId: String) = "$DEVICE_EVENT_PREFIX.user.$userId"
    }
}
