package com.app.services.networking

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiExecutor(
    val client: HttpClient
) {

    suspend inline fun <reified T> executeGet(
        url: String,
        crossinline queryParams: ParametersBuilder.() -> Unit = {},
        crossinline headers: HeadersBuilder.() -> Unit = {}
    ): ApiResponse<T> {

        return try {
            val response = client.get(urlString = url) {
                url {
                    queryParams(parameters)
                }
                headers {
                    headers.invoke(this)
                }
            }

            return ApiResponse.Success(
                statusCode = response.status.value,
                data = response.body()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.Error(e)
        }
    }
}