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

@Composable
fun CryptoScreen(modifier: Modifier = Modifier) {
    val vm: CryptoScreenViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        when (val priceState = state.currentPrice) {
            is CurrentPriceState.Success -> CurrentCoinPrice(priceState.price)
            is CurrentPriceState.Loading -> LoadingIndicator()
            is CurrentPriceState.Error -> ErrorMessage(
                priceState.message,
                if (priceState.retryAllowed) vm::fetchCurrentPrice else null
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
            "%.2f".format(price.eur).trimEnd('0').trimEnd('.', ','),
            style = MaterialTheme.typography.displayMedium
        )
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