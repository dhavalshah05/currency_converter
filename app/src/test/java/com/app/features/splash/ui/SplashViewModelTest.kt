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

@Suppress("PrivatePropertyName")
class SplashViewModelTest : StringSpec() {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var configureExchangeRatesUseCase: ConfigureExchangeRatesUseCase
    private lateinit var SUT: SplashViewModel

    init {
        beforeSpec { Dispatchers.setMain(testDispatcher) }
        afterSpec { Dispatchers.resetMain() }

        beforeEach {
            configureExchangeRatesUseCase = mockk(relaxed = true, relaxUnitFun = true)
            SUT = SplashViewModel(
                configureExchangeRatesUseCase = configureExchangeRatesUseCase
            )
        }

        "init_startConfiguration" {
            // Arrange
            // Act
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) { configureExchangeRatesUseCase.invoke() }
        }

        "init_navigateToDashboard_whenConfigurationSuccess" {
            // Arrange
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