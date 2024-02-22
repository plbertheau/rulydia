package com.plbertheau.domain.usecase

import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(email: String): Flow<Response<UserResponse>> {
        return userRepository.getUser(email).flowOn(Dispatchers.IO)
    }
}