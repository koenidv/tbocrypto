package com.koenidv.tbocrypto.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

enum class Caches {
    CURRENT,
    HISTORIC,
    COINS
}

@ViewModelScoped
class SharedPrefsCache @Inject constructor(
    @ApplicationContext context: Context
) : SimpleCacheInterface, SharedPrefs("cache", context) {

    override fun updateCurrentPrice(value: CurrentPrice) = set(Caches.CURRENT.name, value)
    override fun updateHistoricData(value: HistoricalData) = set(Caches.HISTORIC.name, value)
    override fun updateCoins(value: List<Coin>) = set(Caches.COINS.name, value)

    override fun getLastCurrentPrice(): CurrentPrice? =
        get(Caches.CURRENT.name, CurrentPrice::class.java) as CurrentPrice?

    override fun getLastHistoricData(): HistoricalData? =
        get(Caches.HISTORIC.name, HistoricalData::class.java) as HistoricalData?

    @Suppress("UNCHECKED_CAST")
    override fun getLastCoins(): List<Coin>? =
        (get(Caches.COINS.name, Array<Coin>::class.java) as Array<Coin>?)?.toList()

    override fun getCurrentPriceTimestamp(): Long? = getTimestamp(Caches.CURRENT.name)
    override fun getHistoricDataTimestamp(): Long? = getTimestamp(Caches.HISTORIC.name)
    override fun getCoinsTimestamp(): Long? = getTimestamp(Caches.COINS.name)
    private fun getTimestamp(key: String): Long? {
        return prefs.getString("${key}-timestamp", null)?.toLong()
    }

}