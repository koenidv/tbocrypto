package com.koenidv.tbocrypto.ui.screens.crypto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koenidv.tbocrypto.data.Retrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CryptoScreenViewModel : ViewModel() {

    private val api = Retrofit.api

    private val _uiState = MutableStateFlow(
        CryptoScreenUiState(
            currentPrice = CurrentPriceState.Loading
        )
    )
    val uiState: StateFlow<CryptoScreenUiState> = _uiState.asStateFlow()

    private fun getCurrentPrice(coinId: String) {
        viewModelScope.launch {
            _uiState.update { curr ->
                curr.copy(currentPrice = CurrentPriceState.Loading)
            }
            try {
                api.getCurrentPrice(coinId).getOrElse(coinId) {
                    throw Exception("Could not find expected coinId key in map")
                }.let {
                    _uiState.update { curr ->
                        curr.copy(currentPrice = CurrentPriceState.Success(it))
                    }
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching current price", e)
                _uiState.update { curr ->
                    curr.copy(currentPrice = CurrentPriceState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    init {
        getCurrentPrice("bitcoin")
    }

}