package com.certn.mobile.faceautocapture

import android.graphics.Bitmap
import com.certn.sdk.face.autocapture.CertnIDFaceAutoCaptureResult

data class BasicFaceAutoCaptureResult(
    val bitmap: Bitmap,
    val certnIDFaceAutoCaptureResult: CertnIDFaceAutoCaptureResult
)
