package com.plbertheau.domain.model

import com.google.gson.annotations.SerializedName

data class ResultResponse(
    @SerializedName("results") val items: List<UserResponse> = emptyList(),
    val nextPage: Int? = null
)
