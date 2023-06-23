package com.app.features.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.services.navigator.Navigator
import com.fynd.nitrozen.theme.NitrozenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeNavContractEvents()
        observeScreenEvents()
    }

    private fun observeNavContractEvents() {
        navigator.getResultContracts().selectCurrencyNavResult.observeResult("selected_currency", this) {
            val result = it ?: return@observeResult
            viewModel.onAction(DashboardScreenAction.OnCurrencyChange(result.currency.shortName))
        }
    }

    private fun observeScreenEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToSelectCurrency.collectLatest {
                    navigator.openSelectCurrencyScreen()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                NitrozenTheme {
                    DashboardScreen(
                        state = viewModel.screenState.collectAsState().value,
                        onAction = viewModel::onAction
                    )
                }
            }
        }
    }

}