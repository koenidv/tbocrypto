package com.koenidv.tbocrypto.ui.screens.crypto

import com.koenidv.tbocrypto.data.Coin
import com.koenidv.tbocrypto.data.CurrentPrice
import com.koenidv.tbocrypto.data.HistoricalData
import java.time.Instant

sealed interface RequestState<out T> {
    data object Loading : RequestState<Nothing>
    data class Success<T>(val value: T, val lastFetched: Instant) : RequestState<T>
    data class Error(val message: String, val retryAllowed: Boolean) : RequestState<Nothing>
}

data class CryptoScreenUiState(
    var selectedCoinId: String,
    var coins: RequestState<List<Coin>>,
    var currentPrice: RequestState<CurrentPrice>,
    var historicData: RequestState<HistoricalData>
)