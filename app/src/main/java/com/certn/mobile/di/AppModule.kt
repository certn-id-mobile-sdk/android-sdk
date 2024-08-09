package com.certn.mobile.di

import android.content.Context
import com.certn.mobile.BuildConfig
import com.certn.mobile.InitializeCertnIDSdkUseCase
import com.certn.mobile.data.api.CertnIDService
import com.certn.mobile.documentautocapture.CreateDocumentUiResultUseCase
import com.certn.mobile.faceautocapture.CreateFaceUiResultUseCase
import com.certn.mobile.io.RawResourceCopier
import com.certn.mobile.network.OkHttpFactory
import com.certn.mobile.network.OkHttpLog
import com.certn.mobile.network.interceptors.HeadersInterceptor
import com.certn.mobile.network.interceptors.HttpLoggingInterceptor
import com.certn.mobile.nfcreading.CreateNfcReadingResultUseCase
import com.certn.mobile.nfcreading.ResolveAuthorityCertificatesFileUseCase
import com.certn.mobile.storage.CertnIDEncryptedSharedPreferences
import com.certn.mobile.util.extensions.create
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        encryptedStorage: CertnIDEncryptedSharedPreferences
    ): OkHttpClient {

        val logger = OkHttpLog()

        return if (BuildConfig.PROD_VERSION) {
            OkHttpFactory.createOkHttpClient(
                context,
                setOf(
                    HeadersInterceptor(encryptedStorage),
                    HttpLoggingInterceptor(logger)
                )
            )
        } else {
            OkHttpFactory.makeUnsafeOkHttpClient(
                setOf(
                    HeadersInterceptor(encryptedStorage),
                    HttpLoggingInterceptor(logger)
                )
            )
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Singleton
    @Provides
    fun provideEncryptedStorage(
        @ApplicationContext context: Context
    ): CertnIDEncryptedSharedPreferences = CertnIDEncryptedSharedPreferences(context)

    // use cases

    @Singleton
    @Provides
    fun provideInitializeCertnIDSdkUseCase(
        @ApplicationContext context: Context
    ): InitializeCertnIDSdkUseCase =
        InitializeCertnIDSdkUseCase(context)

    @Singleton
    @Provides
    fun provideCreateDocumentUiResultUseCase(
    ): CreateDocumentUiResultUseCase =
        CreateDocumentUiResultUseCase()

    @Singleton
    @Provides
    fun provideCreateFaceUiResultUseCase(
    ): CreateFaceUiResultUseCase =
        CreateFaceUiResultUseCase()

    @Singleton
    @Provides
    fun provideResolveAuthorityCertificatesFileUseCase(
        @ApplicationContext context: Context
    ): ResolveAuthorityCertificatesFileUseCase =
        ResolveAuthorityCertificatesFileUseCase(
            context = context,
            resourceCopier = RawResourceCopier(context.resources)
        )

    @Singleton
    @Provides
    fun provideCreateNfcReadingResultUseCase(
    ): CreateNfcReadingResultUseCase =
        CreateNfcReadingResultUseCase()

    // services

    @Singleton
    @Provides
    fun provideCertnIDService(retrofit: Retrofit): CertnIDService =
        retrofit.create(CertnIDService::class)

}
