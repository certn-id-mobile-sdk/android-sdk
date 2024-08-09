package com.certn.mobile.ui

import android.util.Base64
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class ByteArraySerializer : JsonSerializer<ByteArray> {

    override fun serialize(
        src: ByteArray?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(encodeBR(src))
    }

    private fun encodeBR(src: ByteArray?): String? = try {
        if (src != null) {
            Base64.encodeToString(src, Base64.NO_WRAP)
        } else null
    } catch (e: Exception) {
        null
    }

}
