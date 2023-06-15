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
    ): T {
        return try {
            val response = client.get(urlString = url) {
                url {
                    queryParams(parameters)
                }
                headers {
                    headers.invoke(this)
                }
            }

            response.body()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}