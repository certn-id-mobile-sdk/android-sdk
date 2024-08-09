package com.certn.mobile.network

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

open class OkHttpLog : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        // Workaround for binary body logging
             if (message.length > 4000) {
                 Timber.tag(OkHttpLog::class.simpleName ?: "").d(message.substring(0, 4000))
                 log(message.substring(4000))
             } else {
                 Timber.tag(OkHttpLog::class.simpleName ?: "").d(message)
             }
       // if (!message.contains("�")) Timber.tag(OkHttpLog::class.simpleName ?: "").d(message)
    }
}
