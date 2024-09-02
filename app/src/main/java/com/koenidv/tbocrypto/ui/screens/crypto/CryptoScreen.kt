package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koenidv.tbocrypto.ui.components.ErrorMessage
import com.koenidv.tbocrypto.ui.components.LoadingIndicator

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