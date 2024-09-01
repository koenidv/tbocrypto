package com.koenidv.tbocrypto.data

interface SimpleCacheInterface {
    fun updateCurrentPrice(value: CurrentPrice)
    fun updateHistoricData(value: HistoricalData)
    fun getLastCurrentPrice(): CurrentPrice?
    fun getLastHistoricData(): HistoricalData?
    fun getCurrentPriceTimestamp(): Long?
    fun getHistoricDataTimestamp(): Long?
    fun clear()
}