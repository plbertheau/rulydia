package com.plbertheau.data.service

import com.plbertheau.data.Constants.DEFAULT_PAGE
import com.plbertheau.data.Constants.DEFAULT_PAGE_SIZE
import com.plbertheau.data.Constants.DEFAULT_SEED
import com.plbertheau.domain.model.ResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {

    @GET(".")
    suspend fun getUsers(
        @Query("page") page: Int = DEFAULT_PAGE,
        @Query("results") results: Int = DEFAULT_PAGE_SIZE,
        @Query("seed") seed: String = DEFAULT_SEED
    ): ResultResponse
}