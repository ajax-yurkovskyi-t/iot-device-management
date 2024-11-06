package com.example.internal

object KafkaTopic {
    private const val REQUEST_PREFIX = "com.example.iotmanagementdevice.output.pub"

    object KafkaDeviceUpdateEvents {
        private const val DEVICE_PREFIX = "$REQUEST_PREFIX.device"

        const val UPDATE = "$DEVICE_PREFIX.update"
        const val NOTIFY = "$DEVICE_PREFIX.notify"
    }
}
