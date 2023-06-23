package com.app.services.navigator.contracts.models

import android.os.Parcelable
import com.app.features.dashboard.data.model.Currency
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectCurrencyNavResult(
    val currency: Currency
) : Parcelable