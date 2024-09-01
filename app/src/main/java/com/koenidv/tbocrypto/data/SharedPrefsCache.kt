package com.koenidv.tbocrypto.data

import android.content.Context
import com.google.gson.Gson
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SharedPrefsCache @Inject constructor(
    @ApplicationContext context: Context
) : SimpleCacheInterface {

    private val prefs = context.getSharedPreferences("cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun updateCurrentPrice(value: CurrentPrice) = set("currentPrice", value)
    override fun updateHistoricData(value: HistoricalData) = set("historicData", value)
    private fun set(key: String, value: Any) {
        prefs.edit()
            .putString(key, gson.toJson(value))
            .putString("${key}-timestamp", System.currentTimeMillis().toString())
            .apply()
    }

    override fun getLastCurrentPrice(): CurrentPrice? =
        get("currentPrice", CurrentPrice::class.java) as CurrentPrice?

    override fun getLastHistoricData(): HistoricalData? =
        get("historicData", HistoricalData::class.java) as HistoricalData?

    private fun <T> get(key: String, classType: Class<T>): Any? {
        val json = prefs.getString(key, null)
        return gson.fromJson(json, classType)
    }

    override fun getCurrentPriceTimestamp(): Long? = getTimestamp("currentPrice")
    override fun getHistoricDataTimestamp(): Long? = getTimestamp("historicData")
    private fun getTimestamp(key: String): Long? {
        return prefs.getString("${key}-timestamp", null)?.toLong()
    }

    override fun clear() = prefs.edit().clear().apply()
}