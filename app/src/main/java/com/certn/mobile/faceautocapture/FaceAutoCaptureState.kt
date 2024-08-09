package com.certn.mobile.faceautocapture

data class FaceAutoCaptureState(
    val isProcessing: Boolean = false,
    val result: BasicFaceAutoCaptureResult? = null,
    val errorMessage: String? = null,
)
