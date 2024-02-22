package com.plbertheau.data.room

import androidx.paging.PagingSource
import androidx.room.*
import com.plbertheau.domain.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<User>)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM users")
    fun getAll(): PagingSource<Int, User>

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getByEmail(email: String): User?
}

