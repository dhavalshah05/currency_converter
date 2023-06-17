package com.app.features.selectCurrency.ui

import com.app.features.dashboard.data.model.Currency

data class SelectCurrencyScreenState(
    val isLoading: Boolean = false,
    val currencies: List<Currency> = emptyList(),
)