package com.koenidv.tbocrypto.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun formatPrice(price: Float): String {
    return "%.2f".format(price).replace(Regex("[.,]00$"), "") + "€"
}

fun formatTimestampTime(timestampSeconds: Long): String {
    val instant = Instant.ofEpochSecond(timestampSeconds) // coingecko returns this timestamp in seconds
    return DateTimeFormatter
        .ofLocalizedTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())
        .format(instant)
}

fun formatTimestampDay(timestampMillis: Long): String {
    val instant = Instant.ofEpochMilli(timestampMillis) // coingecko returns this timestamp in milliseconds
    return DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withZone(ZoneId.from(ZoneOffset.UTC)) // coingecko daily timestamp is 00:00 UTC
        .format(instant)
}