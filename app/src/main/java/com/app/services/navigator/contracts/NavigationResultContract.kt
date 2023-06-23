package com.app.services.navigator.contracts

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner

class NavigationResultContract<T: Parcelable>(private val fragmentManager: FragmentManager) {

    fun observeResult(
        key: String,
        lifecycleOwner: LifecycleOwner,
        callback: (T?) -> Unit
    ) {
        fragmentManager.setFragmentResultListener(key, lifecycleOwner) { _, bundle ->
            val item = bundle.getParcelable<T>(key)
            callback.invoke(item)
        }
    }

    fun setResult(
        key: String,
        result: T
    ) {
        fragmentManager.setFragmentResult(
            key,
            Bundle().apply {
                putParcelable(key, result)
            }
        )
    }
}
