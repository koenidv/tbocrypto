package com.koenidv.tbocrypto.ui.screens.crypto

import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.data.HistoricalData
import java.time.Instant

sealed interface PriceState<out T> {
    data object Loading : PriceState<Nothing>
    data class Success<T>(val value: T, val lastFetched: Instant) : PriceState<T>
    data class Error(val message: String, val retryAllowed: Boolean) : PriceState<Nothing>
}

data class CryptoScreenUiState(
    var selectedCoinId: String,
    var currentPrice: PriceState<CurrentPrice>,
    var historicData: PriceState<HistoricalData>
)