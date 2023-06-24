package com.app.features.selectCurrency.ui

import com.app.features.dashboard.data.model.Currency

sealed interface SelectCurrencyScreenAction {
    data class OnSelectCurrency(val currency: Currency) : SelectCurrencyScreenAction
    object GoBack : SelectCurrencyScreenAction
    data class OnChangeSearchText(val searchText: String) : SelectCurrencyScreenAction
}