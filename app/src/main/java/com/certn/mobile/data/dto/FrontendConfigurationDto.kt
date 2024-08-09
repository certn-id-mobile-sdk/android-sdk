package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.FrontendConfiguration
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class FrontendConfigurationDto(
    @Json(name = "allowManualUpload")
    val allowManualUpload: Boolean?,
    @Json(name = "flipDocumentTime")
    val flipDocumentTime: Int?,
    @Json(name = "logInnoDetectionResults")
    val logInnoDetectionResults: Boolean?,
    @Json(name = "innovatricsLicence")
    val innovatricsLicence: String?
) {

    fun toModel() = FrontendConfiguration(
        allowManualUpload,
        flipDocumentTime,
        logInnoDetectionResults,
        innovatricsLicence
    )

}
