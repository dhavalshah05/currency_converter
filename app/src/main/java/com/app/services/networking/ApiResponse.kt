package com.app.services.networking

sealed class ApiResponse<out T> {

    data class Success<T>(
        val statusCode: Int,
        val data: T,
    ) : ApiResponse<T>()

    data class Error(
        val error: Exception,
    ) : ApiResponse<Nothing>()
}

fun ApiResponse<*>.isSuccess() = this is ApiResponse.Success
fun ApiResponse<*>.isError() = this is ApiResponse.Error