package com.plbertheau.rulydia

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.domain.usecase.GetUserUseCase
import com.plbertheau.rulydia.viewmodel.UserDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class UserDetailViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun `test_userResponse_emits_Loading_state`() = runTest {
        // Given
        val expectedResponse = Response.Loading(UserResponse(email = "user1@exemple.com"))

        val mockUseCase = mock<GetUserUseCase>()
        val mockFlow: Flow<Response<UserResponse>> = flowOf(expectedResponse)
        val mockSavedStateHandle = mock<SavedStateHandle>()
        whenever(mockSavedStateHandle.get<String>("email")).thenReturn("user1@exemple.com")
        whenever(mockUseCase.invoke(any())).thenReturn(mockFlow)
        val viewModel = UserDetailViewModel(mockSavedStateHandle, mockUseCase)
        // When
        val actualDataAfterUserResponse = viewModel.userResponse
            .filterIsInstance<Response<UserResponse>>()
            .take(2)
            .toList()
        // Then
        Assert.assertEquals(expectedResponse, actualDataAfterUserResponse[1])
    }


    @Test
    fun `test_userResponse_emits_Success_state`() = runTest {
        // Given
        val email = "test@example.com"
        val userResponse = UserResponse(email = email)
        val expectedResponse = Response.Success(userResponse)
        // Given

        val mockUseCase = mock<GetUserUseCase>()
        val mockFlow: Flow<Response<UserResponse>> = flowOf(expectedResponse)
        val mockSavedStateHandle = mock<SavedStateHandle>()
        whenever(mockSavedStateHandle.get<String>(any())).thenReturn(email)
        whenever(mockUseCase.invoke(any())).thenReturn(mockFlow)
        val viewModel = UserDetailViewModel(mockSavedStateHandle, mockUseCase)
        // When
        val actualDataAfterUserResponse = viewModel.userResponse
            .filterIsInstance<Response<UserResponse>>()
            .take(2)
            .toList()
        // Then
        Assert.assertEquals(expectedResponse, actualDataAfterUserResponse[1])
    }

    @Test
    fun `test_userResponse_emits_Error_state`() = runTest {
        // Given
        val email = "test@example.com"
        val errorMessage = "An error occurred"
        val expectedResponse = Response.Error(errorMessage, UserResponse())

        val mockUseCase = mock<GetUserUseCase>()
        val mockFlow: Flow<Response<UserResponse>> = flowOf(expectedResponse)
        val mockSavedStateHandle = mock<SavedStateHandle>()
        whenever(mockSavedStateHandle.get<String>(any())).thenReturn(email)
        whenever(mockUseCase.invoke(any())).thenReturn(mockFlow)
        val viewModel = UserDetailViewModel(mockSavedStateHandle, mockUseCase)
        // When
        val actualDataAfterUserResponse = viewModel.userResponse
            .filterIsInstance<Response<UserResponse>>()
            .take(2)
            .toList()
        // Then
        Assert.assertEquals(expectedResponse, actualDataAfterUserResponse[1])
    }

}
