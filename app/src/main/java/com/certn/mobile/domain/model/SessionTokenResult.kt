package com.certn.mobile.domain.model

data class SessionTokenResult(
    val hasError: Boolean?,
    val token: String?,
    val error: String?
)
