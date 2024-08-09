package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.NfcKeyResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class NfcKeyResponseDto(
    @Json(name = "data")
    val data: NfcKeyDataDto?,
    @Json(name = "rfidPresence")
    val rfidPresence: Int?,
    @Json(name = "error")
    val error: String?,
    @Json(name = "hasError")
    val hasError: Boolean?
) {
    fun toModel() = NfcKeyResponse(
        data?.toModel(),
        rfidPresence,
        error,
        hasError
    )
}
