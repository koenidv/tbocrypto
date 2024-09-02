package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.data.HistoricalData
import com.koenidv.tbocrypto.ui.components.ErrorMessage
import com.koenidv.tbocrypto.ui.components.LazyTable
import com.koenidv.tbocrypto.ui.components.LoadingIndicator
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
        RemoteData(state.currentPrice, vm::handleRefreshClicked) {
            CurrentCoinPrice(it)
        }
        RemoteData(state.historicData, vm::handleRefreshClicked) {
            HistoricalCoinData(it)
        }
    }

}

@Composable
fun <T>RemoteData(state: RequestState<T>, handleRefresh: () -> Unit, composable: @Composable (RequestState.Success<T>) -> Unit) {
    when (state) {
        is RequestState.Success -> composable(state)
        is RequestState.Loading -> LoadingIndicator()
        is RequestState.Error -> ErrorMessage(
            state.message,
            if (state.retryAllowed) handleRefresh else null
        )
    }
}

@Composable
fun CurrentCoinPrice(priceState: RequestState.Success<CurrentPrice>) {
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
fun HistoricalCoinData(priceState: RequestState.Success<HistoricalData>) {
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