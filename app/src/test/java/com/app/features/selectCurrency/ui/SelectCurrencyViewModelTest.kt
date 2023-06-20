package com.app.features.selectCurrency.ui

import app.cash.turbine.testIn
import com.app.features.dashboard.data.model.Currency
import com.app.features.selectCurrency.data.GetCurrenciesUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.*

@Suppress("PrivatePropertyName")
class SelectCurrencyViewModelTest : StringSpec() {

    companion object {
        private val CURRENCIES = listOf(
            Currency(shortName = "USD", "United State Dollar"),
            Currency(shortName = "INR", "Indian Rupee"),
        )
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getCurrenciesUseCase: GetCurrenciesUseCase
    private lateinit var SUT: SelectCurrencyViewModel

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        beforeEach {
            getCurrenciesUseCase = mockk()
            coEvery { getCurrenciesUseCase.getCurrencies() } returns CURRENCIES

            SUT = SelectCurrencyViewModel(
                getCurrenciesUseCase = getCurrenciesUseCase
            )
        }

        "init_setInitialScreenState" {
            // Arrange
            val screenStateTurbine = SUT.screenState.testIn(this)

            // Act
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertFalse(actualScreenState.isLoading)
            assertEquals(0, actualScreenState.currencies.size)
        }

        "init_updateLoadingInScreenState" {
            // Arrange
            val screenStateTurbine = SUT.screenState.testIn(this)

            // Act
            screenStateTurbine.awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertTrue(actualScreenState.isLoading)
        }

        "init_updateCurrenciesInScreenState_whenUsecaseReturnCurrencies" {
            // Arrange
            val screenStateTurbine = SUT.screenState.testIn(this)

            // Act
            screenStateTurbine.awaitItem()
            testDispatcher.scheduler.advanceUntilIdle()
            screenStateTurbine.awaitItem()
            val actualScreenState = screenStateTurbine.awaitItem()
            screenStateTurbine.cancel()

            // Assert
            assertEquals(2, actualScreenState.currencies.size)
        }

        "onAction_updateGoBackState_whenSelectCurrency" {
            // Arrange
            val turbine = SUT.goBackWithCurrency.testIn(this)
            val expectedCurrency = CURRENCIES.first()

            // Act
            SUT.onAction(SelectCurrencyScreenAction.OnSelectCurrency(expectedCurrency))

            testDispatcher.scheduler.advanceUntilIdle()
            val result = turbine.awaitItem()
            turbine.cancel()

            assertEquals(result.fullName, expectedCurrency.fullName)
            assertEquals(result.shortName, expectedCurrency.shortName)
        }
    }

}