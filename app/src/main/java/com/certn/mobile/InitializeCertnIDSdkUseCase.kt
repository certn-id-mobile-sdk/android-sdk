package com.certn.mobile

import android.content.Context
import android.content.res.Resources
import com.certn.sdk.CertnIDLibraryType
import com.certn.sdk.CertnIDMobileSdk
import com.certn.sdk.CertnIDSdkConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class InitializeCertnIDSdkUseCase(
    private val context: Context,
    private val certnIDMobileSdk: CertnIDMobileSdk = CertnIDMobileSdk,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke() = withContext(dispatcher) {
        val configuration = createCertnIDSdkConfiguration(context)
        certnIDMobileSdk.initialize(configuration)
    }

    private fun createCertnIDSdkConfiguration(context: Context) = CertnIDSdkConfiguration(
        context = context,
        licenseBytes = readLicenseBytes(context.resources),
        libraries = listOf(
            CertnIDLibraryType.DocumentLibraryType,
            CertnIDLibraryType.FaceBalancedLibraryType,
            CertnIDLibraryType.NfcLibraryType
        )
    )

    private fun readLicenseBytes(resources: Resources) =
        resources.openRawResource(R.raw.certn_id_license).use(InputStream::readBytes)

}
