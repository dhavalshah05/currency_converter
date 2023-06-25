package com.app.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.R
import com.app.features.dashboard.data.model.ConvertedRate
import com.app.features.dashboard.data.model.Currency
import com.fynd.nitrozen.components.button.filled.NitrozenFilledButton
import com.fynd.nitrozen.components.chip.NitrozenChip
import com.fynd.nitrozen.components.divider.NitrozenDivider
import com.fynd.nitrozen.components.textfield.NitrozenTextFieldConfiguration
import com.fynd.nitrozen.components.textfield.NitrozenTextFieldConfiguration.Default
import com.fynd.nitrozen.components.textfield.outlined.NitrozenOutlinedTextField
import com.fynd.nitrozen.theme.NitrozenTheme
import com.fynd.nitrozen.utils.extensions.clickableWithRipple
import java.math.RoundingMode
import java.text.DecimalFormat

@Preview
@Composable
private fun Preview_DashboardScreen() {
    NitrozenTheme {
        DashboardScreen(
            state = DashboardScreenState(),
            onAction = {}
        )
    }
}

@Composable
fun DashboardScreen(
    state: DashboardScreenState,
    onAction: (DashboardScreenAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NitrozenTheme.colors.background)
            .padding(top = 20.dp),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            NitrozenOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                hint = stringResource(id = R.string.hint_amount),
                value = state.amount,
                onValueChange = {
                    onAction(DashboardScreenAction.OnAmountChange(it))
                },
                textFieldState = state.amountState,
                trailingIcon = {
                    NitrozenChip(
                        text = state.selectedCurrency,
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            onAction(DashboardScreenAction.SelectCurrency)
                        },
                        trailing = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_down),
                                contentDescription = "",
                                tint = NitrozenTheme.colors.grey100
                            )
                        }
                    )
                },
                configuration = NitrozenTextFieldConfiguration.Outlined.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                )
            )

            NitrozenFilledButton(
                text = stringResource(id = R.string.button_convert),
                onClick = {
                    onAction(DashboardScreenAction.CalculateExchangeRates)
                },
                enabled = state.shouldEnableConvertButton,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        if (state.convertedRates.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(top = 16.dp, start = 20.dp),
                text = stringResource(id = R.string.label_converted_currencies),
                style = NitrozenTheme.typography.bodySmallBold,
                color = NitrozenTheme.colors.grey80
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(top = 20.dp),
            ) {
                items(state.convertedRates) { item ->
                    ConvertedCurrency(convertedRate = item)
                }
            }
        }
    }
}

@Composable
private fun ConvertedCurrency(
    modifier: Modifier = Modifier,
    convertedRate: ConvertedRate,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1F),
            ) {
                Text(
                    text = getConvertedAmountInString(convertedRate),
                    style = NitrozenTheme.typography.bodyMediumBold,
                    color = NitrozenTheme.colors.grey100
                )
                Text(
                    text = convertedRate.destinationShortName,
                    style = NitrozenTheme.typography.bodySmallRegular,
                    color = NitrozenTheme.colors.grey80,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        NitrozenDivider()
    }
}

@Composable
private fun getConvertedAmountInString(convertedRate: ConvertedRate) =
    DecimalFormat("#.##")
        .apply {
            roundingMode = RoundingMode.DOWN
        }
        .format(convertedRate.destinationAmount)