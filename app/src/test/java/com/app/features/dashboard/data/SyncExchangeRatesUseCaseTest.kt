package com.app.features.dashboard.data

import com.app.services.networking.repositories.OpenExchangeRemoteRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows

@Suppress("LocalVariableName")
class SyncExchangeRatesUseCaseTest : StringSpec({
    "when invoked repository functions are called" {
        val repository = mockk<OpenExchangeRemoteRepository>(
            relaxed = true,
            relaxUnitFun = true
        )

        val SUT = SyncExchangeRatesUseCase(repository)
        SUT.invoke()

        coVerify(exactly = 1) { repository.getCurrencies() }
        coVerify(exactly = 1) { repository.getExchangeRatesForUSD() }
    }

    "throws exception when repository function throws exception" {
        val repository = mockk<OpenExchangeRemoteRepository>(
            relaxed = true,
            relaxUnitFun = true
        ) {
            coEvery { getCurrencies() } throws Exception()
        }

        val SUT = SyncExchangeRatesUseCase(repository)

        assertThrows<Exception> { SUT.invoke() }
        coVerify(exactly = 1) { repository.getCurrencies() }
        coVerify(exactly = 0) { repository.getExchangeRatesForUSD() }

    }
})