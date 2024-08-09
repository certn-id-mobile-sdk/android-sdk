package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.RegisterDocumentResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegisterDocumentResultDto(
    @Json(name = "isTwoSideDocument")
    val isTwoSideDocument: Boolean?,
    @Json(name = "error")
    val error: String?,
    @Json(name = "pageSide")
    val pageSide: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "hasError")
    val hasError: Boolean?
) {
    fun toModel() = RegisterDocumentResult(
        isTwoSideDocument,
        error,
        pageSide,
        country,
        type,
        hasError
    )
}
