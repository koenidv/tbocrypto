package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.data.HistoricalData
import com.koenidv.tbocrypto.ui.components.LazyTable
import com.koenidv.tbocrypto.util.formatPrice
import com.koenidv.tbocrypto.util.formatTimestampDay
import com.koenidv.tbocrypto.util.formatTimestampTime

@Composable
fun CryptoScreen(modifier: Modifier = Modifier) {
    val vm: CryptoScreenViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val priceState = state.currentPrice) {
            is PriceState.Success -> CurrentCoinPrice(priceState)
            is PriceState.Loading -> LoadingIndicator()
            is PriceState.Error -> ErrorMessage(
                priceState.message,
                if (priceState.retryAllowed) vm::handleRefreshClicked else null
            )
        }
        when (val historicDataState = state.historicData) {
            is PriceState.Success -> HistoricalCoinData(historicDataState)
            is PriceState.Loading -> LoadingIndicator()
            is PriceState.Error -> ErrorMessage(
                historicDataState.message,
                if (historicDataState.retryAllowed) vm::handleRefreshClicked else null
            )
        }
    }

}

@Composable
fun CurrentCoinPrice(priceState: PriceState.Success<CurrentPrice>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            formatPrice(priceState.value.eur),
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            stringResource(R.string.last_updated, formatTimestampTime(priceState.value.timestamp)),
        )
        Text(
            stringResource(
                R.string.last_fetched,
                formatTimestampTime(priceState.lastFetched.epochSecond)
            ),
        )
    }
}

@Composable
fun HistoricalCoinData(priceState: PriceState.Success<HistoricalData>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        LazyTable(priceState.value.prices.map {
            listOf(
                formatTimestampDay(it.timestamp),
                formatPrice(it.eur)
            )
        })
    }
    Text(
        stringResource(
            R.string.last_fetched,
            formatTimestampTime(priceState.lastFetched.epochSecond)
        ),
    )
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