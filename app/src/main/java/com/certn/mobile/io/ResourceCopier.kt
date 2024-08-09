package com.certn.mobile.io

import java.io.File

interface ResourceCopier {

    fun copyToFile(resourceId: Int, destinationFile: File)
}
