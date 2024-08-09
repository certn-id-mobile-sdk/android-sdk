package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.NfcKeyData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class NfcKeyDataDto(
    @Json(name = "documentNumber")
    val documentNumber: String?,
    @Json(name = "dateOfBirth")
    val dateOfBirth: String?,
    @Json(name = "dateOfExpiry")
    val dateOfExpiry: String?
) {
    fun toModel() = NfcKeyData(
        documentNumber,
        dateOfBirth,
        dateOfExpiry
    )
}
