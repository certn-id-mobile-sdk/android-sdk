package com.certn.mobile.data.repository

import com.certn.mobile.data.api.CertnIDService
import com.certn.mobile.domain.model.*
import com.certn.mobile.domain.repository.BaseRepository
import com.certn.mobile.domain.repository.CertnIDRepository
import javax.inject.Inject

class CertnIDRepositoryImpl @Inject constructor(
    private val certnIDService: CertnIDService
) : BaseRepository(), CertnIDRepository {

    override suspend fun getTokenExchange(
        token: String
    ): SessionTokenResult =
        apiRequest {
            certnIDService.getTokenExchange(token).toModel()
        }

    override suspend fun getSdkConfiguration(): ConfigurationResponse =
        apiRequest {
            certnIDService.getSdkConfiguration().toModel()
        }

    override suspend fun postEnrolmentStart(): SessionStartResult =
        apiRequest {
            certnIDService.postEnrolmentStart().toModel()
        }

    override suspend fun postEnrolmentDocument(
        registerDocumentRequest: RegisterDocumentRequest
    ): RegisterDocumentResult =
        apiRequest {
            certnIDService.postEnrolmentDocument(registerDocumentRequest.toDto()).toModel()
        }

    override suspend fun postEnrolmentFace(
        registerFaceRequest: RegisterFaceRequest
    ): RegisterFaceResult =
        apiRequest {
            certnIDService.postEnrolmentFace(registerFaceRequest.toDto()).toModel()
        }

    override suspend fun getNfcKeyData(): NfcKeyResponse =
        apiRequest {
            certnIDService.getNfcKeyData().toModel()
        }

    override suspend fun postEnrolmentNfcData(
        registerNfcDataCommand: RegisterNfcDataCommand
    ): RegisterNfcDataCommandResult =
        apiRequest {
            certnIDService.postEnrolmentNfcData(registerNfcDataCommand.toDto()).toModel()
        }

    override suspend fun postEnrolmentComplete(): SessionCompleteCommandResult =
        apiRequest {
            certnIDService.postEnrolmentComplete().toModel()
        }

}
