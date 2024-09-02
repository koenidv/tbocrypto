package com.koenidv.tbocrypto.data

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SharedPrefsCacheTest {
    private lateinit var cache: SharedPrefsCache

    @Before
    fun setUp() {
        cache = SharedPrefsCache(RuntimeEnvironment.getApplication().applicationContext)
    }

    @Test
    fun cacheCurrentPrice() {
        val currentPrice = CurrentPrice(
            53742f,
            1724112000000
        )
        cache.updateCurrentPrice(currentPrice)
        assertEquals(currentPrice, cache.getLastCurrentPrice())
    }

    @Test
    fun cacheHistoricData() {
        val historicData = HistoricalData(
            listOf(
                CurrentPrice(53742f, 1724112000000),
                CurrentPrice(52474f, 1725268360000)
            )
        )
        cache.updateHistoricData(historicData)
        assertEquals(historicData, cache.getLastHistoricData())
    }

    @Test
    fun clearCache() {
        cache.updateCurrentPrice(
            CurrentPrice(
                53742f,
                1724112000000
            )
        )
        cache.updateHistoricData(
            HistoricalData(
                listOf(
                    CurrentPrice(53742f, 1724112000000),
                    CurrentPrice(52474f, 1725268360000)
                )
            )
        )
        cache.clear()
        assertEquals(null, cache.getLastCurrentPrice())
    }

    @After
    fun tearDown() {
        cache.clear()
    }

}