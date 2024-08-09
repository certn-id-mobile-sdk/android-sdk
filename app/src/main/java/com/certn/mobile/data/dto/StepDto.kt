package com.certn.mobile.data.dto

import com.certn.mobile.domain.model.Step
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StepDto(
    @Json(name = "name")
    val name: EnrolmentStep?
) {

    fun toModel() = Step(name)

}
