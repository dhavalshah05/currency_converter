package com.app.features.selectCurrency.ui

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
import com.app.features.dashboard.data.model.Currency
import com.app.services.navigator.Navigator
import com.app.services.navigator.contracts.models.SelectCurrencyNavResult
import com.fynd.nitrozen.theme.NitrozenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectCurrencyFragment : Fragment() {

    private val viewModel: SelectCurrencyViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goBackWithCurrency.collectLatest {
                    selectCurrencyAndGoBack(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goBack.collectLatest {
                    navigator.goBack()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                NitrozenTheme {
                    SelectCurrencyScreen(
                        state = viewModel.screenState.collectAsState().value,
                        onAction = viewModel::onAction
                    )
                }
            }
        }
    }

    private fun selectCurrencyAndGoBack(currency: Currency) {
        navigator.getResultContracts().selectCurrencyNavResult.setResult(SelectCurrencyNavResult(currency))
        navigator.goBack()
    }
}