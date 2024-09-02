package com.koenidv.tbocrypto.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

enum class Caches(name: String) {
    CURRENT("currentPrice"),
    HISTORIC("historicData"),
    COINS("coins")
}

@ViewModelScoped
class SharedPrefsCache @Inject constructor(
    @ApplicationContext context: Context
) : SimpleCacheInterface {

    private val prefs = context.getSharedPreferences("cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun updateCurrentPrice(value: CurrentPrice) = set(Caches.CURRENT.name, value)
    override fun updateHistoricData(value: HistoricalData) = set(Caches.HISTORIC.name, value)
    override fun updateCoins(value: List<Coin>) = set(Caches.COINS.name, value)
    private fun set(key: String, value: Any) {
        Log.d("SharedPrefsCache", "Setting $key to $value")
        prefs.edit()
            .putString(key, gson.toJson(value))
            .putString("${key}-timestamp", System.currentTimeMillis().toString())
            .apply()
    }

    override fun getLastCurrentPrice(): CurrentPrice? =
        get(Caches.CURRENT.name, CurrentPrice::class.java) as CurrentPrice?

    override fun getLastHistoricData(): HistoricalData? =
        get(Caches.HISTORIC.name, HistoricalData::class.java) as HistoricalData?

    @Suppress("UNCHECKED_CAST")
    override fun getLastCoins(): List<Coin>? =
        (get(Caches.COINS.name, Array<Coin>::class.java) as Array<Coin>?)?.toList()

    private fun <T> get(key: String, classType: Class<T>): Any? {
        val json = prefs.getString(key, null)
        return gson.fromJson(json, classType)
    }

    override fun getCurrentPriceTimestamp(): Long? = getTimestamp(Caches.CURRENT.name)
    override fun getHistoricDataTimestamp(): Long? = getTimestamp(Caches.HISTORIC.name)
    override fun getCoinsTimestamp(): Long? = getTimestamp(Caches.COINS.name)
    private fun getTimestamp(key: String): Long? {
        return prefs.getString("${key}-timestamp", null)?.toLong()
    }

    override fun clear() = prefs.edit().clear().apply()
}