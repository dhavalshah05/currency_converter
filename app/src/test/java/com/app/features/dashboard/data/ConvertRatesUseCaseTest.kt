package com.app.features.dashboard.data

import com.app.db.exchangeRates.ExchangeRateEntity
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.app.features.conversionLogs.data.CreateConversionLogUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.*

@Suppress("PrivatePropertyName")
internal class ConvertRatesUseCaseTest : StringSpec() {

    private lateinit var exchangeRateEntityQueriesMock: ExchangeRateEntityQueries
    private lateinit var createConversionLogUseCase: CreateConversionLogUseCase
    private lateinit var SUT: ConvertRatesUseCase

    init {
        beforeEach {
            exchangeRateEntityQueriesMock = mockk(
                relaxed = true,
                relaxUnitFun = true
            ) {
                coEvery { getAllExchangeRates().executeAsList() } returns listOf(
                    ExchangeRateEntity("USD", 1.0),
                    ExchangeRateEntity("INR", 80.0),
                    ExchangeRateEntity("CAD", 30.0),
                    ExchangeRateEntity("PAK", 120.0),
                )
            }

            createConversionLogUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true
            )

            SUT = ConvertRatesUseCase(
                exchangeRateEntityQueries = exchangeRateEntityQueriesMock,
                createConversionLogUseCase = createConversionLogUseCase
            )
        }

        "invoke_throwIllegalArgumentException_whenInvalidShortName" {
            // Arrange

            // Act
            shouldThrow<IllegalArgumentException> {
                SUT.invoke(
                    shortName = "SAD",
                    amount = 30.0
                )
            }

            // Assert
        }

        "invoke_fetchExchangeRatesFromDb_whenValidShortName" {
            // Arrange

            // Act
            val actualConvertedRates = SUT.invoke(
                shortName = "PAK",
                amount = 30.0
            )

            // Assert
            verify(exactly = 1) { exchangeRateEntityQueriesMock
                .getAllExchangeRates()
                .executeAsList()
            }
            actualConvertedRates.size.shouldBeEqual(4)
        }

        "invoke_returnConvertedRates_whenValidShortName" {
            // Arrange

            // Act
            val actualConvertedRates = SUT.invoke(
                shortName = "PAK",
                amount = 30.0
            )

            // For PAK
            val rateForPak = actualConvertedRates.find { it.destinationShortName == "PAK" }
            rateForPak.shouldNotBeNull()
            rateForPak.destinationAmount.shouldBeEqual(30.0)

            // For CAD
            val rateForCAD = actualConvertedRates.find { it.destinationShortName == "CAD" }
            rateForCAD.shouldNotBeNull()
            rateForCAD.destinationAmount.shouldBeEqual(7.5)

            // For INR
            val rateForINR = actualConvertedRates.find { it.destinationShortName == "INR" }
            rateForINR.shouldNotBeNull()
            rateForINR.destinationAmount.shouldBeEqual(20.0)

            // For USD
            val rateForUSD = actualConvertedRates.find { it.destinationShortName == "USD" }
            rateForUSD.shouldNotBeNull()
            rateForUSD.destinationAmount.shouldBeEqual(0.25)
        }

        "given valid shortName and amount - when invoke - then create conversion log" {
            // Arrange
            // Act
            SUT.invoke(
                shortName = "PAK",
                amount = 30.0
            )
            // Assert
            coVerify(exactly = 1) {
                createConversionLogUseCase.create("PAK", 30.0)
            }
        }

    }
}