package com.koenidv.tbocrypto.ui.screens.crypto

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.Coin

@Composable
fun CoinSelectorDialog(
    opened: Boolean,
    coins: List<Coin>,
    onCoinSelected: (Coin) -> Unit,
    onDismissRequest: () -> Unit
) {
    if (opened) {
        var filterText by remember { mutableStateOf("") }
        val filteredCoins = coins.filterByQuery(filterText, 2)

        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column {
                    TextField(
                        value = filterText,
                        onValueChange = { filterText = it },
                        label = { Text(stringResource(R.string.action_search)) })
                    if (filterText.length >= 2) CoinsRow(
                        filteredCoins,
                        onCoinSelected,
                        Modifier.height(48.dp)
                    )
                    else Text(
                        stringResource(R.string.hint_search_coins),
                        modifier = Modifier.height(48.dp)
                    )
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            }
        }
    }
}


@Composable
private fun CoinsRow(
    coins: List<Coin>,
    onCoinSelected: (Coin) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        items(coins) { coin ->
            Button(onClick = { onCoinSelected(coin) }) {
                Text(coin.name)
            }
        }
    }
}

private fun List<Coin>.filterByQuery(query: String, minQueryLength: Int): List<Coin> {
    return if (query.length < minQueryLength) listOf() else this.filter {
        it.name.contains(
            query,
            ignoreCase = true
        )
    }
}