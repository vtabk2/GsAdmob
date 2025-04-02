package com.core.gsadmob.utils.gson

import com.core.gsadmob.model.AdGsType
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class AdGsTypeAdapter : JsonDeserializer<AdGsType>, JsonSerializer<AdGsType> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): AdGsType {
        return try {
            // Convert JSON string to enum, handling case differences
            AdGsType.valueOf(json.asString.uppercase())
        } catch (e: IllegalArgumentException) {
            // Provide default value if unknown type comes from JSON
            AdGsType.INTERSTITIAL
        }
    }

    override fun serialize(src: AdGsType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        // Convert enum to lowercase string in JSON
        return JsonPrimitive(src.name.lowercase())
    }
}