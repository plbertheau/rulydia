package com.plbertheau.domain.usecase

import androidx.paging.PagingData
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserListUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<PagingData<User>> = userRepository.getUsers().flowOn(Dispatchers.IO)
}