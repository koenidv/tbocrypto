package com.koenidv.tbocrypto.data

import com.google.gson.annotations.SerializedName

data class CurrentPrice(
    val eur: Float,
    @SerializedName("last_updated_at")
    val timestamp: Long
)