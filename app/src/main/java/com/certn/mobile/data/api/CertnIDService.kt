package com.certn.mobile.data.api

import com.certn.mobile.data.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CertnIDService {

    @GET(API.PUBLIC.Enrolment.GET_TOKEN_EXCHANGE)
    suspend fun getTokenExchange(
        @Path("token") token: String
    ): SessionTokenResultDto

    @GET(API.PUBLIC.Enrolment.GET_SDK_CONFIGURATION)
    suspend fun getSdkConfiguration(): ConfigurationResponseDto

    @POST(API.PUBLIC.Enrolment.POST_ENROLMENT_START)
    suspend fun postEnrolmentStart(): SessionStartResultDto

    @POST(API.PUBLIC.Enrolment.POST_ENROLMENT_DOCUMENT)
    suspend fun postEnrolmentDocument(
        @Body registerDocumentRequestDto: RegisterDocumentRequestDto
    ): RegisterDocumentResultDto

    @POST(API.PUBLIC.Enrolment.POST_ENROLMENT_FACE)
    suspend fun postEnrolmentFace(
        @Body registerFaceRequestDto: RegisterFaceRequestDto
    ): RegisterFaceResultDto

    @GET(API.PUBLIC.Enrolment.GET_NFC_KEY_DATA)
    suspend fun getNfcKeyData(): NfcKeyResponseDto

    @POST(API.PUBLIC.Enrolment.POST_ENROLMENT_NFC_DATA)
    suspend fun postEnrolmentNfcData(
        @Body registerNfcDataCommandDto: RegisterNfcDataCommandDto
    ): RegisterNfcDataCommandResultDto

    @POST(API.PUBLIC.Enrolment.POST_ENROLMENT_COMPLETE)
    suspend fun postEnrolmentComplete(): SessionCompleteCommandResultDto

    companion object {
        object API {
            object PUBLIC {
                object Enrolment {
                    private const val PREFIX = "api/v1/enrolment"
                    const val GET_TOKEN_EXCHANGE = "$PREFIX/token/exchange/{token}"
                    const val GET_SDK_CONFIGURATION = "$PREFIX/sdk/configuration"
                    const val POST_ENROLMENT_START = "$PREFIX/start"
                    const val POST_ENROLMENT_DOCUMENT = "$PREFIX/document"
                    const val POST_ENROLMENT_FACE = "$PREFIX/face"
                    const val GET_NFC_KEY_DATA = "$PREFIX/nfc/key"
                    const val POST_ENROLMENT_NFC_DATA = "$PREFIX/nfc/data-groups"
                    const val POST_ENROLMENT_COMPLETE = "$PREFIX/complete"
                }
            }
        }
    }

}
