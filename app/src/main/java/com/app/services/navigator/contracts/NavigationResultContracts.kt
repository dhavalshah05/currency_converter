package com.app.services.navigator.contracts

import androidx.fragment.app.FragmentManager
import com.app.services.navigator.contracts.models.SelectCurrencyNavResult

class NavigationResultContracts constructor(
    fragmentManager: FragmentManager
) {
    val selectCurrencyNavResult =
        NavigationResultContract<SelectCurrencyNavResult>(fragmentManager)
}
