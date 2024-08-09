package com.certn.mobile.domain.model

import com.certn.mobile.data.dto.RegisterFaceRequestDto

data class RegisterFaceRequest(
    val imageBase: String? = null
) {
    fun toDto() = RegisterFaceRequestDto(
        imageBase
    )
}
