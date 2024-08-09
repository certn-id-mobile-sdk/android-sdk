package com.certn.mobile.network.interceptors

import com.certn.mobile.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class HttpLoggingInterceptor(logger: HttpLoggingInterceptor.Logger) : Interceptor {

    private val logging = HttpLoggingInterceptor(logger)

    init {
        logging.level =
            if (BuildConfig.PROD_VERSION) {
                HttpLoggingInterceptor.Level.NONE
            } else {
                HttpLoggingInterceptor.Level.BODY
            }
    }

    override fun intercept(chain: Interceptor.Chain): Response = logging.intercept(chain)
}
