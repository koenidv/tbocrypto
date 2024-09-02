package com.koenidv.tbocrypto.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {

    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    val api: CoingeckoInterface by lazy {
        createApi(
            clientWithCoingeckoKey,
            BASE_URL
        )
    }

    fun createApi(
        client: OkHttpClient,
        baseUrl: String
    ): CoingeckoInterface {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CoingeckoInterface::class.java)
    }

    /**
     * Gson instance to handle Coingecko's historical data format
     */
    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                object : TypeToken<HistoricalData>() {}.type,
                HistoricalDataDeserializer()
            )
            .create()
    }

    /**
     * Adds Coingecko API key to Header
     */
    private val clientWithCoingeckoKey by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                if (chain.request().url.host != "api.coingecko.com") {
                    // this _should_ never happen
                    return@addInterceptor chain.proceed(chain.request())
                }
                val requestWithHeader = chain.request().newBuilder()
                    .addHeader("x-cg-demo-api-key", Keys.coingeckoKey())
                    .build()
                chain.proceed(requestWithHeader)
            }
            .build()
    }

}