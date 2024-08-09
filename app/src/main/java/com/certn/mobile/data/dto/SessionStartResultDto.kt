package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.SessionStartResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SessionStartResultDto(
    @Json(name = "hasError")
    val hasError: Boolean?,
    @Json(name = "error")
    val error: String?
) {

    fun toModel() = SessionStartResult(
        hasError,
        error
    )

}
