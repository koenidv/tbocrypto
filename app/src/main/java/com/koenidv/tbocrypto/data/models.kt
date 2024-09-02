package com.koenidv.tbocrypto.data

import com.google.gson.annotations.SerializedName

data class CurrentPrice(
    val eur: Float,
    @SerializedName("last_updated_at")
    val timestamp: Long
)

data class HistoricalData(
    var prices: List<CurrentPrice>
)

data class Coin(
    val id: String,
    val symbol: String,
    val name: String,
)