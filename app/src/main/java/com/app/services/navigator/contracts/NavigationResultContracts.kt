package com.app.services.navigator.contracts

import androidx.fragment.app.FragmentManager
import com.app.services.navigator.contracts.models.SelectCurrencyNavResult

class NavigationResultContracts constructor(
    fragmentManager: FragmentManager
) {
    companion object {
        private const val KEY_CURRENCY = "currency"
    }

    val selectCurrencyNavResult =
        NavigationResultContract<SelectCurrencyNavResult>(KEY_CURRENCY, fragmentManager)
}
