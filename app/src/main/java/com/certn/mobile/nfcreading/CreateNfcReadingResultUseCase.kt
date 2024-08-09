package com.certn.mobile.nfcreading

import com.certn.sdk.nfc.CertnIDTravelDocument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateNfcReadingResultUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(certnIDTravelDocument: CertnIDTravelDocument): NfcReadingResult =
        withContext(ioDispatcher) {
            NfcReadingResult(certnIDTravelDocument)
        }
}
