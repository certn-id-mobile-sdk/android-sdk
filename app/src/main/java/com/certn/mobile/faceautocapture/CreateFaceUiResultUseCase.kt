package com.certn.mobile.faceautocapture

import com.certn.sdk.face.autocapture.CertnIDFaceAutoCaptureResult
import com.certn.sdk.image.CertnIDBitmapFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateFaceUiResultUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(certnIDFaceAutoCaptureResult: CertnIDFaceAutoCaptureResult): BasicFaceAutoCaptureResult =
        withContext(ioDispatcher) {
            BasicFaceAutoCaptureResult(
                bitmap = CertnIDBitmapFactory.create(certnIDFaceAutoCaptureResult.certnIDBgrRawImage),
                certnIDFaceAutoCaptureResult = certnIDFaceAutoCaptureResult
            )
        }
}
