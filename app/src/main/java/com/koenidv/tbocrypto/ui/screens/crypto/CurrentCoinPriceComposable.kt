package com.koenidv.tbocrypto.ui.screens.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.koenidv.tbocrypto.R
import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.util.formatPrice
import com.koenidv.tbocrypto.util.formatTimestampTime

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