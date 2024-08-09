package com.certn.mobile.domain.model

data class SessionCompleteCommandResult(
    val customerReturnUrl: String?,
    val hasError: Boolean?,
    val error: String?
)
