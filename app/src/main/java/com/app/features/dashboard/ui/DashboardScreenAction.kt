package com.app.features.dashboard.ui

sealed interface DashboardScreenAction {
    data class OnAmountChange(val amount: String) : DashboardScreenAction
    data class OnCurrencyChange(val currency: String): DashboardScreenAction
    object CalculateExchangeRates : DashboardScreenAction
    object SelectCurrency : DashboardScreenAction
}