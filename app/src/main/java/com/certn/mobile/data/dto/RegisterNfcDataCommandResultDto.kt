package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.RegisterNfcDataCommandResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterNfcDataCommandResultDto(
    @Json(name = "error")
    val error: String?,
    @Json(name = "hasError")
    val hasError: Boolean?
) {
    fun toModel() = RegisterNfcDataCommandResult(
        error,
        hasError
    )
}
