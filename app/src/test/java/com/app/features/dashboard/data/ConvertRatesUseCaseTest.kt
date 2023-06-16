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

class ConvertRatesUseCaseTest : StringSpec({
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    MyDatabase.Schema.create(driver)

    "given shortName and amount, throw exception if shortName is not available" {
        val exchangeRateEntityQueries = mockk<ExchangeRateEntityQueries>(
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
            exchangeRateEntityQueries = exchangeRateEntityQueries
        )

        assertThrows<IllegalArgumentException> {
            SUT.invoke(
                shortName = "SAD",
                amount = 30.0
            )
        }
    }

    "given shortName and amount, converts to all amounts" {
        val exchangeRateEntityQueries = mockk<ExchangeRateEntityQueries>(
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
            exchangeRateEntityQueries = exchangeRateEntityQueries
        )
        val actualConvertedRates = SUT.invoke(
            shortName = "PAK",
            amount = 30.0
        )

        verify(exactly = 1) { exchangeRateEntityQueries
            .getAllExchangeRates()
            .executeAsList()
        }
        Assertions.assertEquals(4, actualConvertedRates.size)

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
})