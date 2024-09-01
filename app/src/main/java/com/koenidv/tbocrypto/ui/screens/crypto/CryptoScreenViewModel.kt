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
            currentPrice = PriceState.Loading,
            historicData = PriceState.Loading,
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
                    curr.copy(currentPrice = PriceState.Loading)
                }
                api.getCurrentPrice(coinId).getOrElse(coinId) {
                    throw Exception("Could not find expected coinId key in map")
                }.let {
                    _uiState.update { curr ->
                        curr.copy(currentPrice = PriceState.Success(it))
                    }
                }
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = PriceState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching current price: $e")
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = PriceState.Error(
                            e.message ?: "Unknown error",
                            false
                        )
                    )
                }
            }
        }
    }

    /**
     * Fetches the historic price data of the currently selected coin
     * side effect: updates the uiState
     */
    fun fetchHistoricData() {
        fetchHistoricData(_uiState.value.selectedCoinId)
    }

    /**
     * Fetches the historic price data of a coin by its id from coingecko
     * side effect: updates the uiState
     * @param coinId the id of the coin
     * @param days the number of days to look back: default 14
     */
    private fun fetchHistoricData(coinId: String, days: Int = 14) {
        viewModelScope.launch {
            try {
                _uiState.update { curr ->
                    curr.copy(historicData = PriceState.Loading)
                }
                val data = api.getHistoricData(coinId, days)
                _uiState.update { curr ->
                    curr.copy(historicData = PriceState.Success(data))
                }
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        historicData = PriceState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching historic data: $e")
                _uiState.update { curr ->
                    curr.copy(
                        historicData = PriceState.Error(
                            e.message ?: "Unknown error",
                            false
                        )
                    )
                }
            }
        }
    }

    init {
        fetchCurrentPrice()
        fetchHistoricData()
    }

}