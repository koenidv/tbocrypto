package com.koenidv.tbocrypto.ui.screens.crypto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koenidv.tbocrypto.data.Retrofit
import com.koenidv.tbocrypto.data.SharedPrefsCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class CryptoScreenViewModel @Inject constructor(private val cache: SharedPrefsCache) : ViewModel() {

    private val api = Retrofit.api

    private val _uiState = MutableStateFlow(
        CryptoScreenUiState(
            currentPrice = cache.getLastCurrentPrice()?.let {
                RequestState.Success(it, Instant.ofEpochMilli(cache.getCurrentPriceTimestamp() ?: 0))
            } ?: RequestState.Loading,
            historicData = cache.getLastHistoricData()?.let {
                RequestState.Success(it, Instant.ofEpochMilli(cache.getHistoricDataTimestamp() ?: 0))
            } ?: RequestState.Loading,
            selectedCoinId = "bitcoin"
        )
    )
    val uiState: StateFlow<CryptoScreenUiState> = _uiState.asStateFlow()

    /**
     * Sets ui states to loading and fetches all data
     */
    fun handleRefreshClicked() {
        _uiState.update { curr ->
            curr.copy(
                currentPrice = RequestState.Loading,
                historicData = RequestState.Loading
            )
        }
        fetchAll()
    }

    private fun fetchAll() {
        fetchCurrentPrice()
        fetchHistoricData()
    }

    /**
     * Fetches the price of the currently selected coin
     * side effect: updates the uiState
     */
    private fun fetchCurrentPrice() {
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
                api.getCurrentPrice(coinId).getOrElse(coinId) {
                    throw Exception("Could not find expected coinId key in map")
                }.let {
                    _uiState.update { curr ->
                        curr.copy(currentPrice = RequestState.Success(it, Instant.now()))
                    }
                    cache.updateCurrentPrice(it)
                }
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = RequestState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching current price: $e")
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = RequestState.Error(
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
    private fun fetchHistoricData() {
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
                val data = api.getHistoricData(coinId, days)
                // Sort by newest date
                // Also drop the last entry - it's a slightly outdated current value
                data.prices = data.prices.dropLast(1).sortedByDescending { it.timestamp }
                _uiState.update { curr ->
                    curr.copy(historicData = RequestState.Success(data, Instant.now()))
                }
                cache.updateHistoricData(data)
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        historicData = RequestState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching historic data: $e")
                _uiState.update { curr ->
                    curr.copy(
                        historicData = RequestState.Error(
                            e.message ?: "Unknown error",
                            false
                        )
                    )
                }
            }
        }
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            try {
                val coinIds = api.getCoinIds()
                _uiState.update { curr ->
                    curr.copy(coins = RequestState.Success(coinIds, Instant.now()))
                }
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                _uiState.update { curr ->
                    curr.copy(
                        coins = RequestState.Error(
                            "No internet connection",
                            true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching coin ids: $e")
                _uiState.update { curr ->
                    curr.copy(
                        coins = RequestState.Error(
                            e.message ?: "Unknown error",
                            false
                        )
                    )
                }
            }
        }
    }

    init {
        fetchAll()
        fetchCoins()
        startCoroutineTimer()
    }

    private fun startCoroutineTimer(delayMillis: Long = 60000) {
        viewModelScope.launch {
            while (true) { // bound to viewmodel lifecycle
                fetchAll()
                kotlinx.coroutines.delay(delayMillis)
            }
        }
    }

}