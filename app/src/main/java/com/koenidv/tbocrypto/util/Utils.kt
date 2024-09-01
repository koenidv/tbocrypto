package com.koenidv.tbocrypto.util

fun formatPrice(price: Float): String {
    return "%.2f".format(price).replace(Regex("[.,]00$"), "") + "€"
}