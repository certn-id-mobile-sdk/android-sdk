package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.ConfigurationResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ConfigurationResponseDto(
    @Json(name = "steps")
    val steps: List<StepDto>?,
    @Json(name = "frontendConfiguration")
    val frontendConfiguration: FrontendConfigurationDto?,
    @Json(name = "hasError")
    val hasError: Boolean?,
    @Json(name = "error")
    val error: String?
) {

    fun toModel() = ConfigurationResponse(
        steps?.map { it.toModel() },
        frontendConfiguration?.toModel(),
        hasError,
        error
    )

}
