package com.koenidv.tbocrypto.data

object Keys {
    init {
        System.loadLibrary("native-lib")
    }
    external fun coingeckoKey(): String
}