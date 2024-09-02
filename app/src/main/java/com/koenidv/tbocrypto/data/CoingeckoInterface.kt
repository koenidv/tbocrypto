package com.koenidv.tbocrypto.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoingeckoInterface {
    /**
     * Get the current price of a coin in EUR
     * @param coinId the id of the coin
     * @return a single-entry map with the coin id as key and the current price as value, where timestamp is the last updated time
     */
    @GET("simple/price?vs_currencies=eur&include_last_updated_at=true")
    suspend fun getCurrentPrice(@Query("ids") coinId: String): Map<String, CurrentPrice>

    /**
     * Get the historic price data of a coin in EUR
     * @param coinId the id of the coin
     * @param days the number of days to look back: default 14
     * @param interval the interval of the data (daily, hourly, minutely): default daily
     * @return a list of CurrentPrice objects, where timestamp is 00:00 UTC of the respective day
     */
    @GET("coins/{coinId}/market_chart?vs_currency=eur")
    suspend fun getHistoricData(
        @Path("coinId") coinId: String,
        @Query("days") days: Int = 14,
        @Query("interval") interval: String = "daily"
    ): HistoricalData

    @GET("coins/list")
    suspend fun getCoinIds(): List<Coin>
}