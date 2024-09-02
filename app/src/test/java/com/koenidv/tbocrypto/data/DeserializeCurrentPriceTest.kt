package com.koenidv.tbocrypto.data

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Tests the deserialization of the current price data
 */
class DeserializeCurrentPriceTest {
    private val mockServer = MockWebServer()
    private lateinit var api: CoingeckoInterface

    @Before
    fun setUp() {
        mockServer.start()
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    "{\"bitcoin\": {\"eur\": 52467,\"last_updated_at\": 1725268046}}"
                )
        )
        api = Retrofit.createApi(
            OkHttpClient(),
            mockServer.url("/").toString()
        )
    }


    @Test
    fun test() {
        var response: Map<String, CurrentPrice>
        runBlocking {
            response = api.getCurrentPrice("bitcoin")
        }
        println(response)
        assertEquals(52467f, response["bitcoin"]!!.eur, 0f)
        assertEquals("1725268046", response["bitcoin"]!!.timestamp.toString())
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

}