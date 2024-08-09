package com.certn.mobile.domain.model

data class NfcKeyResponse(
    val data: NfcKeyData?,
    val rfidPresence: Int?,
    val error: String?,
    val hasError: Boolean?
)
