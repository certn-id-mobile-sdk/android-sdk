package com.certn.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CertnIDApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.RELEASE_VERSION) Timber.plant(Timber.DebugTree())
    }

}
