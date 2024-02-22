package com.plbertheau.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.domain.repository.UserRepository
import com.plbertheau.domain.usecase.GetUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetUserUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `invoke_should_return_user_response`() = runTest {
        // Given
        val email = "test@example.com"
        val expectedUserResponse = Response.Success(UserResponse(email = "user1@exemple.com"))
        val mockRepository: UserRepository = mock()
        val mockFlow: Flow<Response<UserResponse>> = flowOf(expectedUserResponse)
        val getUserUseCase = GetUserUseCase(mockRepository)
        whenever(mockRepository.getUser(email)).thenReturn(mockFlow)

        // When
        val actualResponse = getUserUseCase(email).first()

        // Then
        assertEquals(
            expectedUserResponse.data!!.email,
            (actualResponse as Response.Success).data!!.email
        )
    }

    @Test
    fun `invoke_should_return_error_response`() = runTest {
        // Given
        val email = "test@example.com"
        val expectedError = Throwable("User not found")
        val mockRepository: UserRepository = mock()
        val mockFlow: Flow<Response<UserResponse>> =
            flowOf(Response.Error(expectedError.message!!, null))
        val getUserUseCase = GetUserUseCase(mockRepository)
        whenever(mockRepository.getUser(email)).thenReturn(mockFlow)

        // When
        val actualResponse = getUserUseCase(email).first()

        // Then
        assertEquals(expectedError.message, (actualResponse as Response.Error).error)
    }
}