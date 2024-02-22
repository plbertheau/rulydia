package com.plbertheau.domain.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @field:SerializedName("phone") val phone: String = "",
    @field:SerializedName("name") val nom: NameResponse? = null,
    @field:SerializedName("picture") val picture: PictureResponse? = null,
    @field:SerializedName("location") val locationResponse: LocationResponse? = null,
    @field:SerializedName("email") val email: String = ""
)


data class PictureResponse(
    @field:SerializedName("large") val large: String = "",
    @field:SerializedName("medium") val medium: String = "",
    @field:SerializedName("thumbnail") val thumbnail: String = ""
)

data class NameResponse(
    @field:SerializedName("title") val title: String = "",
    @field:SerializedName("first") val first: String = "",
    @field:SerializedName("last") val last: String = ""
)

data class LocationResponse(
    @field:SerializedName("city") val city: String? = "",
    @field:SerializedName("state") val state: String? = ""
)