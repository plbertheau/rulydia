package com.plbertheau.domain.repository

import androidx.paging.PagingData
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<PagingData<User>>
    fun getUser(email: String): Flow<Response<UserResponse>>
}