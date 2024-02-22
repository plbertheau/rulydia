package com.plbertheau.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @field:SerializedName("id") val id: Long = 0L,
    @field:SerializedName("name") val name: String? = "",
    @field:SerializedName("phone") val phone: String = "",
    @field:SerializedName("image_large") val imageLarge: String = "",
    @field:SerializedName("image_medium") val imageMedium: String = "",
    @field:SerializedName("image_thumbnail") val imageThumbnail: String = "",
    @field:SerializedName("street") val locationStreet: String? = "",
    @field:SerializedName("state") val locationState: String? = "",
    @field:SerializedName("city") val locationCity: String? = "",
    @PrimaryKey @field:SerializedName("email") val email: String = ""
) : Serializable