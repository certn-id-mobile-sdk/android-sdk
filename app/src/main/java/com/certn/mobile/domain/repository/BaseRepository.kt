package com.certn.mobile.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T> apiRequest(suspendBlock: suspend () -> T) = withContext(Dispatchers.IO) {
        try {
            suspendBlock.invoke()
        } catch (e: Throwable) {
            throw e
        }
    }
}
