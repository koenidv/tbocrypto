package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.HistoricalData
import com.koenidv.tbocrypto.ui.components.LazyTable
import com.koenidv.tbocrypto.util.formatPrice
import com.koenidv.tbocrypto.util.formatTimestampDay
import com.koenidv.tbocrypto.util.formatTimestampTime

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