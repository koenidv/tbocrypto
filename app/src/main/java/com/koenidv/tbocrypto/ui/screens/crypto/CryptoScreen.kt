package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.Coin
import com.koenidv.tbocrypto.ui.components.ErrorMessage
import com.koenidv.tbocrypto.ui.components.LoadingIndicator

@Composable
fun CryptoScreen(modifier: Modifier = Modifier) {
    val vm: CryptoScreenViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    var openCoinSelectorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RemoteData(state.coins, {}) {
            Button({ openCoinSelectorDialog = true }) { Text(state.selectedCoin.name) }
            CoinSelectorDialog(
                openCoinSelectorDialog,
                it.value,
                { coin ->
                    vm.handleCoinSelected(coin);
                    openCoinSelectorDialog = false
                },
                { openCoinSelectorDialog = false }
            )
        }
        RemoteData(state.currentPrice, vm::handleRefreshClicked) {
            CurrentCoinPrice(it)
        }
        RemoteData(state.historicData, vm::handleRefreshClicked) {
            HistoricalCoinData(it)
        }
    }

}

@Composable
fun <T> RemoteData(
    state: RequestState<T>,
    handleRefresh: () -> Unit,
    composable: @Composable (RequestState.Success<T>) -> Unit
) {
    when (state) {
        is RequestState.Success -> composable(state)
        is RequestState.Loading -> LoadingIndicator()
        is RequestState.Error -> ErrorMessage(
            state.message,
            if (state.retryAllowed) handleRefresh else null
        )
    }
}
