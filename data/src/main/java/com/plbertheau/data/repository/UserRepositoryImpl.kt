package com.plbertheau.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.plbertheau.data.mapper.toUserResponse
import com.plbertheau.data.room.UserLocalDB
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * Implementation of the UserRepository interface that interacts with the user data sources.
 * This class is responsible for fetching user data from both remote and local sources.
 */
class UserRepositoryImpl @Inject constructor(
    private val userPager: Pager<Int, User>,
    private val userDatabase: UserLocalDB
) : UserRepository {

    /**
     * Retrieves a flow of paged user data from the remote source.
     *
     * @return A Flow representing the paged user data.
     */
    override fun getUsers(): Flow<PagingData<User>> {
        return userPager.flow.map { pagingData ->
            pagingData.map { it }
        }
    }

    /**
     * Retrieves user data associated with the specified email address.
     *
     * @param email The email address of the user to retrieve.
     * @return A Flow representing the response containing the user data.
     */
    override fun getUser(email: String): Flow<Response<UserResponse>> = flow {
        val localData: User? = userDatabase.getUserDao().getByEmail(email)
        if (localData != null) emit(Response.Success(localData.toUserResponse()))
    }
}