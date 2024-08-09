package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.RegisterFaceResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterFaceResultDto(
    @Json(name = "error")
    val error: String?,
    @Json(name = "hasError")
    val hasError: Boolean?
) {
    fun toModel() = RegisterFaceResult(
        error,
        hasError
    )
}
