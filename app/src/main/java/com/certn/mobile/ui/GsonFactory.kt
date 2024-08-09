package com.certn.mobile.ui

import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun createGson(): Gson {
    return GsonBuilder()
        .setExclusionStrategies(ResultExclusionStrategy())
        .registerTypeAdapter(ByteArray::class.java, ByteArraySerializer())
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create()
}
