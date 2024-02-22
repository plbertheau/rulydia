package com.plbertheau.rulydia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.usecase.GetUserListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Create an instance of `UserListViewModel` by passing an instance of `GetUserListUseCase` as a constructor parameter.
 * Observe the `userPagingDataFlow` property to receive updates to the user list data.
 */
@HiltViewModel
class UserListViewModel @Inject constructor(
    getUserList: GetUserListUseCase
) :
    ViewModel() {

    val userPagingDataFlow: Flow<PagingData<User>> = getUserList()
        .cachedIn(viewModelScope)
}
