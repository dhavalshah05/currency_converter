package com.app.features.conversionLogs.ui

import app.cash.turbine.turbineScope
import com.app.features.conversionLogs.data.GetConversionLogsUseCase
import com.app.features.conversionLogs.data.model.ConversionLog
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

internal class ConversionLogsViewModelTest : StringSpec() {

    companion object {
        private val CONVERSION_LOGS = listOf(
            ConversionLog(1, "USD", 20.0),
            ConversionLog(2, "INR", 20.0),
        )
    }

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getConversionLogsUseCase: GetConversionLogsUseCase
    private lateinit var SUT: ConversionLogsViewModel

    init {
        beforeSpec {
            Dispatchers.setMain(testDispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
        }

        beforeEach {
            getConversionLogsUseCase = mockk(
                relaxed = true,
                relaxUnitFun = true
            )
            coEvery { getConversionLogsUseCase.getAll() } returns CONVERSION_LOGS

            SUT = ConversionLogsViewModel(
                getConversionLogsUseCase = getConversionLogsUseCase
            )
        }

        "given ViewModel - when init - then show loading" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                turbine.awaitItem()
                testDispatcher.scheduler.advanceUntilIdle()
                val actualScreenState = turbine.awaitItem()
                turbine.cancel()

                // Assert
                actualScreenState.isLoading.shouldBeTrue()
            }
        }

        "given ViewModel - when init - then get conversion logs" {
            // Arrange

            // Act
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) {
                getConversionLogsUseCase.getAll()
            }
        }

        "given database with conversion logs - when init - then update conversion logs in screen state" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                testDispatcher.scheduler.advanceUntilIdle()
                val actualScreenState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                actualScreenState.conversionLogs.shouldNotBeEmpty()
                actualScreenState.conversionLogs.size.shouldBeEqual(2)
            }
        }

        "given database with conversion logs - when init - then update loading in screen state" {
            turbineScope {
                // Arrange
                val turbine = SUT.screenState.testIn(this)

                // Act
                testDispatcher.scheduler.advanceUntilIdle()
                val actualScreenState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                actualScreenState.isLoading.shouldBeFalse()
            }
        }

        "given ideal screen - when action go back - then update go back state" {
            turbineScope {
                // Arrange
                val turbine = SUT.goBack.testIn(this)

                // Act
                SUT.onAction(ConversionLogsActions.GoBack)
                testDispatcher.scheduler.advanceUntilIdle()
                val actualState = turbine.expectMostRecentItem()
                turbine.cancel()

                // Assert
                actualState.shouldNotBeNull()
                actualState.shouldBeEqual(Unit)
            }
        }
    }

}