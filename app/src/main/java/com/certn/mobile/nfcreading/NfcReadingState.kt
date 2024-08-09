package com.certn.mobile.nfcreading

import com.certn.sdk.nfc.CertnIDTravelConfiguration

data class NfcReadingState(
    val certnIDTravelConfiguration: CertnIDTravelConfiguration? = null,
    val result: NfcReadingResult? = null,
    val errorMessage: String? = null
)
