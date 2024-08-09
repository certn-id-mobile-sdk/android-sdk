package com.certn.mobile.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class EnrolmentStep {
    @Json(name = "Welcome")
    Welcome,

    @Json(name = "Document")
    Document,

    @Json(name = "Face")
    Face,

    @Json(name = "ProofOfAddress")
    ProofOfAddress

}
