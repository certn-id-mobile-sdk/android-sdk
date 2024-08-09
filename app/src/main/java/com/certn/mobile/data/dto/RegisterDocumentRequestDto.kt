package com.certn.mobile.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterDocumentRequestDto(
    @Json(name = "imageBase")
    val imageBase: String?,
    @Json(name = "disableQualityCheck")
    val disableQualityCheck: Boolean
)
