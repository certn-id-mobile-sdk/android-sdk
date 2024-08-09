package com.certn.mobile.image

import android.graphics.Bitmap
import com.certn.sdk.image.CertnIDImage
import com.gemalto.jp2.JP2Decoder

fun CertnIDImage.createBitmap(): Bitmap {
    return when {
        this.isJPEG2000Format() -> createBitmapFromJpeg2000(this.image.bytes)
        else -> createBitmap(this.image.bytes)
    }
}

private fun createBitmapFromJpeg2000(bytes: ByteArray): Bitmap {
    val jp2Decoder = JP2Decoder(bytes)
    return jp2Decoder.decode()
}

private fun createBitmap(bytes: ByteArray): Bitmap {
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
