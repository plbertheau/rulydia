package com.plbertheau.data.mapper

import com.plbertheau.domain.entity.User
import com.plbertheau.domain.model.LocationResponse
import com.plbertheau.domain.model.NameResponse
import com.plbertheau.domain.model.PictureResponse
import com.plbertheau.domain.model.UserResponse


fun UserResponse.toUser() = User(
    name = "${nom?.first} ${nom?.last}",
    imageLarge = picture?.large ?: "",
    imageMedium = picture?.medium ?: "",
    imageThumbnail = picture?.thumbnail ?: "",
    phone = phone,
    locationState = locationResponse?.state,
    locationCity = locationResponse?.city,
    email = email
)

fun User.toUserResponse() = UserResponse(
    phone = phone,
    nom = NameResponse(
        first = name?.split(" ")?.get(0) ?: "",
        last = name?.split(" ")?.get(1) ?: ""
    ),
    picture = PictureResponse(
        large = imageLarge,
        medium = imageMedium,
        thumbnail = imageThumbnail
    ),
    locationResponse = LocationResponse(
        city = locationCity,
        state = locationState,
    ),
    email = email
)


