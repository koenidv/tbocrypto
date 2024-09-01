package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.ui.components.LazyTable
import com.koenidv.tbocrypto.util.formatPrice
import com.koenidv.tbocrypto.util.formatTimestampDay

@Composable
fun CryptoScreen(modifier: Modifier = Modifier) {
    val vm: CryptoScreenViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        when (val priceState = state.currentPrice) {
            is PriceState.Success -> CurrentCoinPrice(priceState.value)
            is PriceState.Loading -> LoadingIndicator()
            is PriceState.Error -> ErrorMessage(
                priceState.message,
                if (priceState.retryAllowed) vm::fetchCurrentPrice else null
            )
        }
        when (val historicDataState = state.historicData) {
            is PriceState.Success -> HistoricalCoinData(historicDataState.value.prices)
            is PriceState.Loading -> LoadingIndicator()
            is PriceState.Error -> ErrorMessage(
                historicDataState.message,
                if (historicDataState.retryAllowed) vm::fetchHistoricData else null
            )
        }
    }

}

@Composable
fun CurrentCoinPrice(price: CurrentPrice) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            formatPrice(price.eur),
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Composable
fun HistoricalCoinData(prices: List<CurrentPrice>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        LazyTable(prices.map {
            listOf(
                formatTimestampDay(it.timestamp),
                formatPrice(it.eur)
            )
        })
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String, retryAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error
        )
        retryAction?.let { action ->
            TextButton(action) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Preview
@Composable
fun CryptoScreenPreview() {
    CryptoScreen()
}