package com.app.features.conversionLogs.data

import com.app.db.conversionLog.ConversionLogEntityQueries
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify

@Suppress("PrivatePropertyName")
internal class CreateConversionLogUseCaseTest : StringSpec() {

    companion object {
        private const val SHORT_NAME = "USD"
        private const val AMOUNT = 20.0
    }

    private lateinit var conversionLogEntityQueries: ConversionLogEntityQueries
    private lateinit var SUT: CreateConversionLogUseCase

    init {
        beforeEach {
            conversionLogEntityQueries = mockk(
                relaxed = true,
                relaxUnitFun = true
            )

            SUT = CreateConversionLogUseCase(
                conversionLogEntityQueries = conversionLogEntityQueries
            )
        }

        "given valid shortName and amount - when create - then call database function to create conversionLog" {
            // Arrange
            // Act
            SUT.create(SHORT_NAME, AMOUNT)
            // Assert
            verify(exactly = 1) { conversionLogEntityQueries.createConversionLog(
                id = null,
                shortName = SHORT_NAME,
                amount = AMOUNT
            ) }
        }
    }

}