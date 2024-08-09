package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.SessionCompleteCommandResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SessionCompleteCommandResultDto(
    @Json(name = "customerReturnUrl")
    val customerReturnUrl: String?,
    @Json(name = "hasError")
    val hasError: Boolean?,
    @Json(name = "error")
    val error: String?
) {

    fun toModel() = SessionCompleteCommandResult(
        customerReturnUrl,
        hasError,
        error
    )

}
