package com.certn.mobile.network

import android.content.Context
import com.certn.mobile.BuildConfig

import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object OkHttpFactory {

    fun createOkHttpClient(context: Context, interceptors: Set<Interceptor>): OkHttpClient {
        val sslContext = createSSLContext()
        val trustManagers = createTrustManagers(context)
        sslContext?.init(null, trustManagers, null)
        val sslSocketFactory = sslContext?.socketFactory

        val builder = OkHttpClient().newBuilder()
            .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.COMPATIBLE_TLS))
            .hostnameVerifier(CustomHostnameVerifier(OkHttpClient().hostnameVerifier))
            .readTimeout(BuildConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(BuildConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
        sslSocketFactory?.apply {
            builder.sslSocketFactory(this, trustManagers?.get(0) as X509TrustManager)
        }

        interceptors.forEach { builder.addInterceptor(it) }
        return builder.build()
    }

    // NOTE: used for penetration testing
    // NOTE: No ssl pinning
    fun makeUnsafeOkHttpClient(interceptors: Set<Interceptor>): OkHttpClient {

        val noCheckTrustManager = object : X509TrustManager {

            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(noCheckTrustManager), null)
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient().newBuilder()
            .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(sslSocketFactory, noCheckTrustManager)
            .hostnameVerifier { _, _ -> true }
            .readTimeout(BuildConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(BuildConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)

        interceptors.forEach { builder.addInterceptor(it) }
        return builder.build()
    }

    private fun createSSLContext(): SSLContext? {
        try {
            return try {
                SSLContext.getInstance("TLSv1.2")
            } catch (e1: NoSuchAlgorithmException) {
                try {
                    SSLContext.getInstance("TLSv1.1")
                } catch (e2: NoSuchAlgorithmException) {
                    SSLContext.getInstance("SSL")
                }
            }
        } catch (e: java.lang.Exception) {
            Timber.e("OkHttpFactory", "Can't create SSL socket factory", e)
        }
        return null
    }

    @Throws(
        KeyStoreException::class,
        IOException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class
    )
    fun getTrustStore(context: Context): KeyStore? {
        val inputStream: InputStream = context.assets.open("trust.bks")
        return inputStream.use { inpStr ->
            val trusted = KeyStore.getInstance("BKS")
            trusted.load(inpStr, "__".toCharArray())
            trusted
        }
    }

    @Throws(
        CertificateException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        IOException::class
    )
    fun createTrustManagers(context: Context): Array<TrustManager?>? {
        val trustStore = getTrustStore(context)
        return trustStore?.let { createKeyStoreTrustManagers(it) }
    }

    @Throws(
        NoSuchAlgorithmException::class,
        KeyStoreException::class
    )
    fun createKeyStoreTrustManagers(trustStore: KeyStore?): Array<TrustManager?>? {
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(trustStore)
        return trustManagerFactory.trustManagers
    }

}
