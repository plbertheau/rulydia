package com.plbertheau.rulydia.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel class responsible for managing user detail data and providing it to the UI components.
 * This class is designed to work with the Hilt dependency injection library in Android.
 */
@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUseCase: GetUserUseCase,
) : ViewModel() {

    // Fetch user detail data using the provided GetUserUseCase and SavedStateHandle,
    // and create a StateFlow to represent the response.
    val userResponse: StateFlow<Response<UserResponse>> =
        getUserUseCase(savedStateHandle.get<String>("email")!!).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            Response.Loading(),
        )
}