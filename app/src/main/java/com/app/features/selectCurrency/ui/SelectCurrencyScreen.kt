package com.app.features.selectCurrency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.R
import com.app.features.dashboard.data.model.Currency
import com.fynd.nitrozen.components.appbar.NitrozenAppBar
import com.fynd.nitrozen.components.divider.NitrozenDivider
import com.fynd.nitrozen.components.textfield.outlined.NitrozenOutlinedTextField
import com.fynd.nitrozen.theme.NitrozenTheme
import com.fynd.nitrozen.utils.extensions.clickableWithRipple

@Preview
@Composable
private fun Preview_SelectCurrencyScreen_Loading() {
    NitrozenTheme {
        SelectCurrencyScreen(
            state = SelectCurrencyScreenState(
                isLoading = true
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun Preview_SelectCurrencyScreen_Currencies() {
    NitrozenTheme {
        SelectCurrencyScreen(
            state = SelectCurrencyScreenState(
                isLoading = false,
                currencies = listOf(
                    Currency(shortName = "USD", fullName = "United State Dollars"),
                    Currency(shortName = "INR", fullName = "Indian Rupee"),
                )
            ),
            onAction = {}
        )
    }
}

@Composable
fun SelectCurrencyScreen(
    state: SelectCurrencyScreenState,
    onAction: (SelectCurrencyScreenAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NitrozenTheme.colors.background),
    ) {
        NitrozenAppBar(
            title = stringResource(id = R.string.label_select_currency),
            leading = {
                IconButton(onClick = {
                    onAction(SelectCurrencyScreenAction.GoBack)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_white),
                        contentDescription = "Back",
                        tint = NitrozenTheme.colors.inverse
                    )
                }
            }
        )

        NitrozenOutlinedTextField(
            modifier = Modifier
                .padding(all = 20.dp),
            hint = stringResource(id = R.string.hint_search_here),
            value = state.searchText,
            onValueChange = {
                onAction(SelectCurrencyScreenAction.OnChangeSearchText(it))
            }
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
            ) {
                val items = if (state.searchText.isBlank())
                    state.currencies
                else
                    state.filteredCurrencies

                items(items) { currency ->
                    CurrencyItem(
                        currency = currency,
                        onClick = {
                            onAction.invoke(SelectCurrencyScreenAction.OnSelectCurrency(currency = currency))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyItem(
    modifier: Modifier = Modifier,
    currency: Currency,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickableWithRipple { onClick() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = currency.shortName,
                style = NitrozenTheme.typography.bodyMediumBold,
                color = NitrozenTheme.colors.grey100
            )
            Text(
                text = currency.fullName,
                style = NitrozenTheme.typography.bodySmallRegular,
                color = NitrozenTheme.colors.grey80,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        NitrozenDivider()
    }
}