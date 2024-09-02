package com.koenidv.tbocrypto.ui.screens.crypto

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koenidv.tbocrypto.data.HistoricalData
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
                RequestState.Success(
                    it,
                    Instant.ofEpochMilli(cache.getCurrentPriceTimestamp() ?: 0)
                )
            } ?: RequestState.Loading,
            historicData = cache.getLastHistoricData()?.let {
                RequestState.Success(
                    it,
                    Instant.ofEpochMilli(cache.getHistoricDataTimestamp() ?: 0)
                )
            } ?: RequestState.Loading,
            coins = RequestState.Loading,
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
        fetch(
            suspend { api.getCurrentPrice(coinId) },
            { data ->
                data.getOrElse(coinId) {
                    throw Exception("Could not find expected coinId key in map")
                }.let {
                    _uiState.update { curr ->
                        curr.copy(currentPrice = RequestState.Success(it, Instant.now()))
                    }
                    cache.updateCurrentPrice(it)
                }
            },
            { message: String, retryAllowed: Boolean ->
                _uiState.update { curr ->
                    curr.copy(
                        currentPrice = RequestState.Error(
                            message,
                            retryAllowed
                        )
                    )
                }
            }
        )
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
        fetch(
            suspend { api.getHistoricData(coinId, days) },
            { data ->
                // Sort by newest date
                // Also drop the last entry - it's a slightly outdated current value
                data.prices = data.prices.dropLast(1).sortedByDescending { it.timestamp }
                _uiState.update { curr ->
                    curr.copy(
                        historicData = RequestState.Success(
                            data,
                            Instant.now()
                        )
                    )
                }
                cache.updateHistoricData(data)
            },
            { message: String, retryAllowed: Boolean ->
                _uiState.update { curr ->
                    curr.copy(
                        historicData = RequestState.Error(
                            message,
                            retryAllowed
                        )
                    )
                }
            }
        )
    }

    /**
     * Fetches the list of coin ids from coingecko
     * side effect: updates the uiState
     */
    private fun fetchCoins() {
        fetch(
            suspend { api.getCoinIds() },
            { data ->
                _uiState.update { curr ->
                    curr.copy(coins = RequestState.Success(data, Instant.now()))
                }
            },
            { message: String, retryAllowed: Boolean ->
                _uiState.update { curr ->
                    curr.copy(
                        coins = RequestState.Error(
                            message,
                            retryAllowed
                        )
                    )
                }
            }
        )
    }

    /**
     * Generic fetch function with error handling
     * @param fetchFn the suspend function to fetch data
     * @param updateFn the function to update the uiState with the fetched data
     * @param errorUpdateFn the function to update the uiState with an error message
     */
    private fun <T> fetch(
        fetchFn: suspend () -> T,
        updateFn: (T) -> Unit,
        errorUpdateFn: (message: String, retryAllowed: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val data = fetchFn()
                updateFn(data)
            } catch (unknownHostE: UnknownHostException) {
                // User is most likely offline
                // Last known data will not be shown
                errorUpdateFn("No internet connection", true)
            } catch (e: Exception) {
                Log.e("CryptoScreenViewModel", "Error fetching data: $e")
                errorUpdateFn(e.message ?: "Unknown error", false)
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