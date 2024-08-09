package com.certn.mobile.network.interceptors

import android.util.Base64
import com.certn.mobile.storage.CertnIDEncryptedSharedPreferences
import com.certn.mobile.util.Constants
import com.certn.mobile.util.Constants.HEADER_AUTHORIZATION
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeadersInterceptor(
    private val encryptedStorage: CertnIDEncryptedSharedPreferences
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().apply {
            newBuilder().also {
                head(this, it, HEADER_AUTHORIZATION,
                    encryptedStorage.getValue(Constants.ACCESS_TOKEN)
                        ?.let { accessToken -> "Bearer $accessToken" } ?: "Basic ${base64encode()}"
                )
                return chain.proceed(it.build())
            }
        }
    }

    private fun base64encode() =
        Base64.encodeToString(
            "--".toByteArray(),
            Base64.NO_WRAP or Base64.URL_SAFE
        )

    private fun head(
        request: Request,
        builder: Request.Builder,
        headerKey: String,
        headerValue: String
    ) {
        if (request.header(headerKey) == null) {
            builder.header(headerKey, headerValue)
        }
    }
}
