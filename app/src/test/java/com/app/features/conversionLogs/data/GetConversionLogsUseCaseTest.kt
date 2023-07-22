package com.app.features.conversionLogs.data

import com.app.db.conversionLog.ConversionLogEntity
import com.app.db.conversionLog.ConversionLogEntityQueries
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@Suppress("PrivatePropertyName")
internal class GetConversionLogsUseCaseTest : StringSpec() {

    companion object {
        private val CONVERSION_LOG_ENTITIES = listOf(
            ConversionLogEntity(1, "USD", 20.0),
            ConversionLogEntity(2, "INR", 30.0),
        )
    }

    private lateinit var conversionLogEntityQueries: ConversionLogEntityQueries
    private lateinit var SUT: GetConversionLogsUseCase

    init {
        beforeEach {
            conversionLogEntityQueries = mockk(
                relaxed = true,
                relaxUnitFun = true
            )
            every { conversionLogEntityQueries.getAllConversionLogs().executeAsList() } returns CONVERSION_LOG_ENTITIES

            SUT = GetConversionLogsUseCase(
                conversionLogEntityQueries = conversionLogEntityQueries
            )
        }

        "given useCase - when getAll - then call database function to get conversion logs" {
            // Arrange
            // Act
            SUT.getAll()
            // Assert
            verify(exactly = 1) { conversionLogEntityQueries.getAllConversionLogs().executeAsList() }
        }

        "given empty database - when getAll - then return empty list" {
            // Arrange
            every { conversionLogEntityQueries.getAllConversionLogs().executeAsList() } returns emptyList()

            // Act
            val actualList = SUT.getAll()

            // Assert
            actualList.shouldBeEmpty()
        }

        "given non empty database - when getAll - then return conversion logs" {
            // Arrange

            // Act
            val actualList = SUT.getAll()

            // Assert
            actualList.shouldNotBeEmpty()
            actualList.size.shouldBeEqual(2)
        }
    }

}