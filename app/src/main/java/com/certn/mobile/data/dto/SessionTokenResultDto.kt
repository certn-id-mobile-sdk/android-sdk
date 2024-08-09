package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.SessionTokenResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SessionTokenResultDto(
    @Json(name = "hasError")
    val hasError: Boolean?,
    @Json(name = "token")
    val token: String?,
    @Json(name = "error")
    val error: String?
) {

    fun toModel() = SessionTokenResult(
        hasError,
        token,
        error
    )

}
