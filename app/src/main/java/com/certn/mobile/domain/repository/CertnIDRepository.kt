package com.certn.mobile.domain.repository

import com.certn.mobile.domain.model.*

interface CertnIDRepository {

    suspend fun getTokenExchange(token: String): SessionTokenResult

    suspend fun getSdkConfiguration(): ConfigurationResponse

    suspend fun postEnrolmentStart(): SessionStartResult

    suspend fun postEnrolmentDocument(
        registerDocumentRequest: RegisterDocumentRequest
    ): RegisterDocumentResult

    suspend fun postEnrolmentFace(
        registerFaceRequest: RegisterFaceRequest
    ): RegisterFaceResult

    suspend fun getNfcKeyData(): NfcKeyResponse

    suspend fun postEnrolmentNfcData(
        registerNfcDataCommand: RegisterNfcDataCommand
    ): RegisterNfcDataCommandResult

    suspend fun postEnrolmentComplete(): SessionCompleteCommandResult

}
