package com.certn.mobile.domain.model

import com.certn.mobile.data.dto.RegisterDocumentRequestDto

data class RegisterDocumentRequest(
    val imageBase: String? = null,
    val disableQualityCheck: Boolean = true
) {
    fun toDto() = RegisterDocumentRequestDto(
        imageBase,
        disableQualityCheck
    )
}
