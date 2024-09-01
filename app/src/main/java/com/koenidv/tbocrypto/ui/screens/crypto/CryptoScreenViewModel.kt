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
import java.net.UnknownHostException

class CryptoScreenViewModel : ViewModel() {

    private val api = Retrofit.api

    private val _uiState = MutableStateFlow(
        CryptoScreenUiState(
            currentPrice = CurrentPriceState.Loading,
            selectedCoinId = "bitcoin"
        )
    )
    val uiState: StateFlow<CryptoScreenUiState> = _uiState.asStateFlow()

    /**
     * Fetches the price of the currently selected coin
     * side effect: updates the uiState
     */
    fun fetchCurrentPrice() {
        fetchCurrentPrice(_uiState.value.selectedCoinId)
    }

    /**
     * Fetches the price of a coin by its id from coingecko
     * side effect: updates the uiState
     * @param coinId the id of the coin
     */
    private fun fetchCurrentPrice(coinId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { curr ->
                    curr.copy(currentPrice = CurrentPriceState.Loading)
                }
                api.getCurrentPrice(coinId).getOrElse(coinId) {
                    throw Exception("Could not find expected coinId key in map")
                }.let {
                    _uiState.update { curr ->
                        curr.copy(currentPrice = CurrentPriceState.Success(it))
                    }
                }
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = CurrentPriceState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching current price: $e")
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = CurrentPriceState.Error(
                            e.message ?: "Unknown error",
                            false
                        )
                    )
                }
            }
        }
    }

    init {
        fetchCurrentPrice("bitcoin")
    }

}