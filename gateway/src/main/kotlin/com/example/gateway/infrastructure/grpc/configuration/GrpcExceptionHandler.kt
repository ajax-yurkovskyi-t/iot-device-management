package com.example.gateway.infrastructure.grpc.configuration

import com.example.core.exception.EntityNotFoundException
import com.google.protobuf.Any
import com.google.rpc.Code
import com.google.rpc.ErrorInfo
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import com.google.rpc.Status as RpcStatus

@GrpcAdvice
class GrpcExceptionHandler {

    @GrpcExceptionHandler(EntityNotFoundException::class)
    fun handleResourceNotFoundException(cause: EntityNotFoundException): StatusRuntimeException {
        val errorInfo = ErrorInfo.newBuilder().apply {
            reason = "entity_not_found"
            domain = "Device"
        }.build()

        val status = RpcStatus.newBuilder().apply {
            code = Code.NOT_FOUND.number
            message = cause.message
            addDetails(Any.pack(errorInfo))
        }.build()

        return StatusProto.toStatusRuntimeException(status)
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleException(cause: Exception): StatusRuntimeException {
        val errorInfo = ErrorInfo.newBuilder().apply {
            reason = "internal_server_error"
        }.build()

        val status = RpcStatus.newBuilder().apply {
            code = Code.INTERNAL.number
            message = cause.message
            addDetails(Any.pack(errorInfo))
        }.build()
        return StatusProto.toStatusRuntimeException(status)
    }
}
