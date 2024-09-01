package com.koenidv.tbocrypto.ui.screens.crypto

import com.koenidv.tbocrypto.data.CurrentPrice

sealed interface CurrentPriceState {
    data object Loading : CurrentPriceState
    data class Success(val price: CurrentPrice) : CurrentPriceState
    data class Error(val message: String, val retryAllowed: Boolean) : CurrentPriceState
}

data class CryptoScreenUiState(
    var currentPrice: CurrentPriceState,
    var selectedCoinId: String,
)