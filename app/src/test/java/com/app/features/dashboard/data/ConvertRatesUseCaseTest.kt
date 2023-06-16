package com.app.features.dashboard.data

import com.app.db.MyDatabase
import com.app.db.exchangeRates.ExchangeRateEntity
import com.app.db.exchangeRates.ExchangeRateEntityQueries
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.assertThrows

class ConvertRatesUseCaseTest : StringSpec() {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    init {
        MyDatabase.Schema.create(driver)

        "invoke_invalidShortName_throwsIllegalArgumentException" {
            // Arrange
            val exchangeRateEntityQueriesMock = mockk<ExchangeRateEntityQueries>(
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

            val SUT = ConvertRatesUseCase(
                exchangeRateEntityQueries = exchangeRateEntityQueriesMock
            )

            // Act
            assertThrows<IllegalArgumentException> {
                SUT.invoke(
                    shortName = "SAD",
                    amount = 30.0
                )
            }
        }

        "invoke_validShortName_exchangeRatesFetchedFromDb" {
            // Arrange
            val exchangeRateEntityQueriesMock = mockk<ExchangeRateEntityQueries>(
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

            val SUT = ConvertRatesUseCase(
                exchangeRateEntityQueries = exchangeRateEntityQueriesMock
            )

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
            Assertions.assertEquals(4, actualConvertedRates.size)
        }

        "invoke_validShortName_convertedRatesReturned" {
            // Arrange
            val exchangeRateEntityQueriesMock = mockk<ExchangeRateEntityQueries>(
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

            val SUT = ConvertRatesUseCase(
                exchangeRateEntityQueries = exchangeRateEntityQueriesMock
            )

            // Act
            val actualConvertedRates = SUT.invoke(
                shortName = "PAK",
                amount = 30.0
            )

            // For PAK
            val rateForPak = actualConvertedRates.find { it.destinationShortName == "PAK" }
            Assertions.assertNotNull(rateForPak)
            requireNotNull(rateForPak)
            Assertions.assertEquals(30.0, rateForPak.destinationAmount)

            // For CAD
            val rateForCAD = actualConvertedRates.find { it.destinationShortName == "CAD" }
            Assertions.assertNotNull(rateForCAD)
            requireNotNull(rateForCAD)
            Assertions.assertEquals(7.5, rateForCAD.destinationAmount)

            // For INR
            val rateForINR = actualConvertedRates.find { it.destinationShortName == "INR" }
            Assertions.assertNotNull(rateForINR)
            requireNotNull(rateForINR)
            Assertions.assertEquals(20.0, rateForINR.destinationAmount)

            // For USD
            val rateForUSD = actualConvertedRates.find { it.destinationShortName == "USD" }
            Assertions.assertNotNull(rateForUSD)
            requireNotNull(rateForUSD)
            Assertions.assertEquals(0.25, rateForUSD.destinationAmount)
        }


    }
}