package com.certn.mobile.documentautocapture

import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureResult
import com.certn.sdk.image.CertnIDBitmapFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateDocumentUiResultUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(certnIDDocumentAutoCaptureResult: CertnIDDocumentAutoCaptureResult): BasicDocumentAutoCaptureResult =
        withContext(ioDispatcher) {
            BasicDocumentAutoCaptureResult(
                bitmap = CertnIDBitmapFactory.create(certnIDDocumentAutoCaptureResult.certnIDBgraRawImage),
                certnIDDocumentAutoCaptureResult = certnIDDocumentAutoCaptureResult
            )
        }

}
