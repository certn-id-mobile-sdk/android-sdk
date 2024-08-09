package com.certn.mobile.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterNfcDataCommandDto(
    @Json(name = "nfcBase")
    val nfcBase: String
)
