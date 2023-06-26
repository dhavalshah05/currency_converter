package com.app.services.networking

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*

class ApiExecutorTest : StringSpec() {
    init {
        "given client return 200 - when executeGet - then return Result" {
            // Arrange
            val client = HttpClient(MockEngine { request ->
                respond(
                    content = "RESULT",
                    status = HttpStatusCode.OK,
                )
            })
            val SUT = ApiExecutor(client)

            // Act
            val result: String = SUT.executeGet("")

            // Assert
            result.shouldBeEqual("RESULT")
        }

        "given client throw exception - when executeGet - then throw exception" {
            // Arrange
            val client = HttpClient(MockEngine { request ->
                throw Exception("Sample Exception")
            })
            val SUT = ApiExecutor(client)

            // Act

            // Assert
            shouldThrow<Exception> { SUT.executeGet("") }
        }
    }
}