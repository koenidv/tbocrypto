package com.koenidv.tbocrypto.data

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class HistoricalDataDeserializer : JsonDeserializer<HistoricalData> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): HistoricalData {
        val pricesJson = json.asJsonObject.getAsJsonArray("prices")
        return HistoricalData(
            pricesJson.map {
                CurrentPrice(
                    eur = it.asJsonArray[1].asFloat,
                    timestamp = it.asJsonArray[0].asLong
                )
            }
        )
    }
}
