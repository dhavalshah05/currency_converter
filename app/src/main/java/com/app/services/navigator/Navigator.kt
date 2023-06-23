package com.app.services.navigator

import com.app.services.navigator.contracts.NavigationResultContracts

interface Navigator {
    fun getResultContracts(): NavigationResultContracts
    fun goBack()
    fun openSelectCurrencyScreen()
    fun openDashboardScreen()
}