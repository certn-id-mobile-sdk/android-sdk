package com.certn.mobile.domain.model

data class RegisterDocumentResult(
    val isTwoSideDocument: Boolean?,
    val error: String?,
    val pageSide: String?,
    val country: String?,
    val type: String?,
    val hasError: Boolean?
)
