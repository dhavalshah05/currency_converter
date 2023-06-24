package com.app.features.dashboard.ui

import com.app.features.dashboard.data.model.ConvertedRate

data class DashboardScreenState(
    val amount: String = "0",
    val selectedCurrency: String = "USD",
    val convertedRates: List<ConvertedRate> = emptyList(),
    val isLoading: Boolean = false,
)
