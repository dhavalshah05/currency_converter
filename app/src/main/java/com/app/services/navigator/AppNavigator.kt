package com.app.services.navigator

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.app.R
import com.app.services.navigator.contracts.NavigationResultContracts

class AppNavigator(
    private val activity: AppCompatActivity,
) : Navigator {

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    private val navController: NavController
        get() {
            val navHostFragment =
                fragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            return navHostFragment.navController
        }

    private val childFragmentManager: FragmentManager
        get() = fragmentManager.fragments.first().childFragmentManager

    private val navigationResultContracts = NavigationResultContracts(fragmentManager)

    private fun getRootFragmentId(): Int {
        return navController.visibleEntries.value[0].destination.id
    }

    private fun isAvailableInStack(@IdRes id: Int): Boolean {
        return navController.visibleEntries.value.map { it.destination.id }.contains(id)
    }

    override fun getResultContracts(): NavigationResultContracts {
        return navigationResultContracts
    }

    override fun goBack() {
        val isPopped = navController.popBackStack()
        if (!isPopped) {
            activity.finish()
        }
    }

    override fun openSelectCurrencyScreen() {
        navController.navigate(R.id.selectCurrencyFragment)
    }

    override fun openDashboardScreen() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(getRootFragmentId(), true)
            .build()
        navController.navigate(R.id.dashboardFragment, null, navOptions)
    }

}