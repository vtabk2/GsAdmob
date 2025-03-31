package com.core.gsadmob.utils

import com.core.gsadmob.model.AdGsType
import com.core.gsadmob.model.AdPlaceName
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object GsonUtils {
    private val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(AdGsType::class.java, AdGsTypeAdapter())
            .create()
    }

    fun parseAdPlaceNameList(json: String): List<AdPlaceName> {
        val type = object : TypeToken<List<AdPlaceName>>() {}.type
        return gson.fromJson(json, type)
    }
}