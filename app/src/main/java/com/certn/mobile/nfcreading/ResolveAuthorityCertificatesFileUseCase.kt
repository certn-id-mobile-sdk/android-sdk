package com.certn.mobile.nfcreading

import android.content.Context
import com.certn.mobile.R
import com.certn.mobile.io.ResourceCopier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ResolveAuthorityCertificatesFileUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context,
    private val resourceCopier: ResourceCopier
) {

    private val filename = "authority_certificates.pem"

    suspend operator fun invoke() = withContext(ioDispatcher) {
        resolveAuthorityCertificatesFile()
    }

    private fun resolveAuthorityCertificatesFile() = File(context.filesDir, filename).apply {
        writeAuthorityCertificatesToFile(this)
    }

    private fun writeAuthorityCertificatesToFile(file: File) =
        resourceCopier.copyToFile(R.raw.authority_certificates, file)
}
