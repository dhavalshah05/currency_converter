package com.app.features.splash.ui

import app.cash.turbine.testIn
import com.app.features.dashboard.data.ConfigureExchangeRatesUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*

class SplashViewModelTest : StringSpec() {

    private val testDispatcher = StandardTestDispatcher()

    init {
        beforeSpec { Dispatchers.setMain(testDispatcher) }
        afterSpec { Dispatchers.resetMain() }

        "init_configurationStarted" {
            // Arrange
            val configureExchangeRatesUseCase = mockk<ConfigureExchangeRatesUseCase>(relaxed = true, relaxUnitFun = true)
            val SUT = SplashViewModel(
                configureExchangeRatesUseCase = configureExchangeRatesUseCase
            )

            // Act
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) { configureExchangeRatesUseCase.invoke() }
        }

        "init_configureSuccess_navigateToDashboard" {
            // Arrange
            val configureExchangeRatesUseCase = mockk<ConfigureExchangeRatesUseCase>(relaxed = true, relaxUnitFun = true)
            val SUT = SplashViewModel(
                configureExchangeRatesUseCase = configureExchangeRatesUseCase
            )

            // Act
            val turbine = SUT.navigateToDashboard.testIn(this)
            testDispatcher.scheduler.advanceUntilIdle()
            val result = turbine.awaitItem()
            turbine.cancel()

            // Assert
            assertNotNull(result)
        }
    }

}