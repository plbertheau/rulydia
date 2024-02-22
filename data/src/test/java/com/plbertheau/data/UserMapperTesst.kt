package com.plbertheau.data

import com.plbertheau.data.mapper.toUser
import com.plbertheau.domain.model.LocationResponse
import com.plbertheau.domain.model.NameResponse
import com.plbertheau.domain.model.PictureResponse
import com.plbertheau.domain.model.UserResponse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserMapperTest {

    @Test
    fun `given an UserResponse to toUser should return an user with the same data`() {
        val userResponse = UserResponse(
            nom = NameResponse("", FAKE_NAME, FAKE_LASTNAME),
            email = FAKE_EMAIL,
            phone = FAKE_PHONE,
            picture = PictureResponse(large = FAKE_PIC),
            locationResponse = LocationResponse(FAKE_STREET, FAKE_CITY)
        )

        val result = userResponse.toUser()

        with(userResponse) {
            assertTrue(result.email == email)
            assertTrue(result.phone == phone)
            assertTrue(result.imageLarge == picture?.large)
            assertTrue(result.locationCity == "${locationResponse?.city}")
        }
    }

    @Test
    fun `given an UserResponse without image to toUser should return an user without image`() {
        val userResponse = UserResponse(
            nom = NameResponse("", FAKE_NAME, FAKE_LASTNAME)
        )

        val result = userResponse.toUser()

        assertTrue(result.imageLarge.isEmpty())
    }

    companion object {
        const val FAKE_NAME = "FAKE_NAME"
        private const val FAKE_PIC = "FAKE_PIC"
        private const val FAKE_LASTNAME = "FAKE_LASTNAME"
        private const val FAKE_EMAIL = "FAKE_EMAIL"
        private const val FAKE_PHONE = "FAKE_PHONE"
        private const val FAKE_STREET = "FAKE_STREET"
        private const val FAKE_CITY = "FAKE_CITY"
    }
}