package com.koenidv.tbocrypto.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CoingeckoInterface {
    /**
     * Get the current price of a coin in EUR
     * @param coinId the id of the coin
     * @return a single-entry map with the coin id as key and the current price as value
     */
    @GET("simple/price?vs_currencies=eur&include_last_updated_at=true")
    suspend fun getCurrentPrice(@Query("ids") coinId: String): Map<String, CurrentPrice>

    // todo historic data
    // todo coin ids
}