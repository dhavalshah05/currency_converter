package com.app.services.networking

import io.kotest.core.spec.style.StringSpec
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows

class ApiExecutorTest : StringSpec({
    "return result when call with valid data" {
        val client = HttpClient(MockEngine { request ->
            respond(
                content = "RESULT",
                status = HttpStatusCode.OK,
            )
        })

        val SUT = ApiExecutor(client)
        val result: String = SUT.executeGet("")
        Assertions.assertEquals("RESULT", result)
    }

    "throw exception if request throws exception" {
        val client = HttpClient(MockEngine { request ->
            throw Exception("Sample Exception")
        })

        val SUT = ApiExecutor(client)
        assertThrows<Exception> {
            val result: String = SUT.executeGet("")
        }

    }
})