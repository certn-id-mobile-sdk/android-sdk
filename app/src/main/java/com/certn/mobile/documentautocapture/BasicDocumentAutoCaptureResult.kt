package com.certn.mobile.documentautocapture

import android.graphics.Bitmap
import com.certn.sdk.document.autocapture.CertnIDDocumentAutoCaptureResult

data class BasicDocumentAutoCaptureResult(
    val bitmap: Bitmap,
    val certnIDDocumentAutoCaptureResult: CertnIDDocumentAutoCaptureResult
)
