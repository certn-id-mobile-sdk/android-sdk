package com.certn.mobile.domain.model

import com.certn.mobile.data.dto.RegisterNfcDataCommandDto

data class RegisterNfcDataCommand(
    val nfcBase: String
) {

    fun toDto() = RegisterNfcDataCommandDto(
        nfcBase
    )

}
