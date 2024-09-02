package com.koenidv.tbocrypto.data

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeserializeCoinListTest {
    private val mockServer = MockWebServer()
    private lateinit var api: CoingeckoInterface

    @Before
    fun setUp() {
        mockServer.start()
        mockServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                "[{\"id\": \"01coin\",\"symbol\": \"zoc\",\"name\": \"01coin\"},{\"id\": \"0chain\",\"symbol\": \"zcn\",\"name\": \"Zus\"}]"
            )
        )
        api = Retrofit.createApi(
            OkHttpClient(), mockServer.url("/").toString()
        )
    }


    @Test
    fun test() {
        var response: List<Coin>
        runBlocking {
            response = api.getCoinIds()
        }
        println(response)

        assertEquals(2, response.size)
        assertEquals(response[0], Coin("01coin", "zoc", "01coin"))
        assertEquals(response[1], Coin("0chain", "zcn", "Zus"))
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

}