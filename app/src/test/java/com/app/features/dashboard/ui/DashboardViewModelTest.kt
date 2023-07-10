package com.app.features.dashboard.ui

import app.cash.turbine.turbineScope
import com.app.features.dashboard.data.ConvertRatesUseCase
import com.app.features.dashboard.data.model.ConvertedRate
import com.fynd.nitrozen.components.textfield.TextFieldState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.ktor.util.reflect.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@Suppress("PrivatePropertyName")
class DashboardViewModelTest : StringSpec() {

    companion object {
        private const val AMOUNT = "10"
        private const val CURRENCY = "INR"

        private val CONVERTED_RATES = listOf(
            ConvertedRate(
                sourceShortName = CURRENCY,
                destinationShortName = "USD",
                destinationAmount = 100.0,
                destinationAmountBase = 20.0
            ),
            ConvertedRate(
                sourceShortName = CURRENCY,
                destinationShortName = "CAD",
                destinationAmount = 50.0,
                destinationAmountBase = 5.0
            )
        )
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var convertRatesUseCase: ConvertRatesUseCase
    private lateinit var SUT: DashboardViewModel

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        beforeEach {
            convertRatesUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true,
            ) {
                coEvery { invoke(shortName = CURRENCY, amount = AMOUNT.toDouble()) } returns CONVERTED_RATES
            }

            SUT = DashboardViewModel(
                convertRatesUseCase = convertRatesUseCase
            )
        }

        "given ViewModel - when init - then set initial screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.amount.shouldBeEqual("0")
                actualScreenState.amountState.shouldBeInstanceOf<TextFieldState.Idle>()
                actualScreenState.isLoading.shouldBeFalse()
                actualScreenState.selectedCurrency.shouldBeEqual("USD")
                actualScreenState.convertedRates.size.shouldBeEqual(0)
            }
        }

        "given valid amount - when change amount - then update amount in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.amount.shouldBeEqual(AMOUNT)
            }
        }

        "given empty amount - when change amount - then set amount error in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(DashboardScreenAction.OnAmountChange(""))
                val actualScreenState = screenStateTurbine.expectMostRecentItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.amountState.shouldBeInstanceOf<TextFieldState.Error>()
            }
        }

        "given invalid amount - when change amount - then set amount error in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                SUT.onAction(DashboardScreenAction.OnAmountChange("90..00"))
                val actualScreenState = screenStateTurbine.expectMostRecentItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.amountState.shouldBeInstanceOf<TextFieldState.Error>()
            }
        }

        "given valid currency - when change currency - then update currency in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.selectedCurrency.shouldBeEqual(CURRENCY)
            }
        }

        "given valid currency - when change currency - then clear converted rates in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)
                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                SUT.onAction(DashboardScreenAction.CalculateExchangeRates)
                testDispatcher.scheduler.advanceUntilIdle()

                // Act
                SUT.onAction(DashboardScreenAction.OnCurrencyChange("NEW_CURRENCY"))
                val actualScreenState = screenStateTurbine.expectMostRecentItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.convertedRates.size.shouldBeEqual(0)
            }
        }

        "given valid amount - when change amount - then clear converted rates in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)
                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                SUT.onAction(DashboardScreenAction.CalculateExchangeRates)
                testDispatcher.scheduler.advanceUntilIdle()

                // Act
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT.plus(1.0)))
                val actualScreenState = screenStateTurbine.expectMostRecentItem()
                screenStateTurbine.cancel()

                // Assert
                actualScreenState.convertedRates.size.shouldBeEqual(0)
            }
        }

        "given valid amount and currency - when calculate exchange rate - then update converted rates in screen state" {
            turbineScope {
                // Arrange
                val screenStateTurbine = SUT.screenState.testIn(this)

                // Act
                screenStateTurbine.awaitItem()
                SUT.onAction(DashboardScreenAction.OnAmountChange(AMOUNT))
                screenStateTurbine.awaitItem()

                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))
                screenStateTurbine.awaitItem()

                SUT.onAction(DashboardScreenAction.CalculateExchangeRates)
                testDispatcher.scheduler.advanceUntilIdle()
                val loadingStartedScreenState = screenStateTurbine.awaitItem()
                val actualScreenState = screenStateTurbine.awaitItem()
                screenStateTurbine.cancel()

                // Assert
                loadingStartedScreenState.isLoading.shouldBeTrue()
                actualScreenState.convertedRates.size.shouldBeEqual(2)
                actualScreenState.isLoading.shouldBeFalse()
            }
        }

        "given 0 amount - when calculate exchange rate - then display error message" {
            turbineScope {
                // Arrange
                val turbine = SUT.errorMessage.testIn(this)
                SUT.onAction(DashboardScreenAction.OnAmountChange("0"))
                SUT.onAction(DashboardScreenAction.OnCurrencyChange(CURRENCY))

                // Act
                SUT.onAction(DashboardScreenAction.CalculateExchangeRates)
                testDispatcher.scheduler.advanceUntilIdle()
                val result = turbine.awaitItem()
                turbine.cancel()

                // Assert
                result.shouldBeEqual("Amount should be greater than 0")
            }
        }

        "given ideal screen - when select currency - then navigate to select currency" {
            turbineScope {
                // Arrange
                val turbine = SUT.navigateToSelectCurrency.testIn(this)

                // Act
                SUT.onAction(DashboardScreenAction.SelectCurrency)
                testDispatcher.scheduler.advanceUntilIdle()
                val result = turbine.awaitItem()
                turbine.cancel()

                // Assert
                result.shouldNotBeNull()
            }
        }
    }

}
