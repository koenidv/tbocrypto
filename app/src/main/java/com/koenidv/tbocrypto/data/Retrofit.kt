package com.koenidv.tbocrypto.data

import com.koenidv.tbocrypto.Keys
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {

    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    val api: CoingeckoInterface by lazy {
        retrofit.create(CoingeckoInterface::class.java)
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientWithCoingeckoKey)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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