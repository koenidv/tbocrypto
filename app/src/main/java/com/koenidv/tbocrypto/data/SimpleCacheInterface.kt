package com.koenidv.tbocrypto.data

interface SimpleCacheInterface {
    fun updateCurrentPrice(value: CurrentPrice)
    fun updateHistoricData(value: HistoricalData)
    fun updateCoins(value: List<Coin>)
    fun getLastCurrentPrice(): CurrentPrice?
    fun getLastHistoricData(): HistoricalData?
    fun getLastCoins(): List<Coin>?
    fun getCurrentPriceTimestamp(): Long?
    fun getHistoricDataTimestamp(): Long?
    fun getCoinsTimestamp(): Long?
    fun clear()
}