package com.koenidv.tbocrypto

object Keys {
    init {
        System.loadLibrary("native-lib")
    }
    external fun coingeckoKey(): String
}