package com.certn.mobile.domain.model

data class ConfigurationResponse(
    val steps: List<Step>?,
    val frontendConfiguration: FrontendConfiguration?,
    val hasError: Boolean?,
    val error: String?
)
