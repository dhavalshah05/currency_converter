package com.app.features.dashboard.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val shortName: String,
    val fullName: String,
) : Parcelable