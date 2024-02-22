package com.plbertheau.data.module

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.plbertheau.data.Constants
import com.plbertheau.data.Constants.BASE_URL
import com.plbertheau.data.Constants.DEFAULT_PAGE_SIZE
import com.plbertheau.data.repository.UserRepositoryImpl
import com.plbertheau.data.room.UserLocalDB
import com.plbertheau.data.service.UserRemoteMediator
import com.plbertheau.data.service.UsersApi
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val cache = Cache(context.cacheDir, cacheSize)
        val builder = OkHttpClient.Builder()
        return builder
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideUsersApi(okHttpClient: OkHttpClient): UsersApi {
        val api: UsersApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(UsersApi::class.java)
        }
        return api
    }

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserLocalDB {
        return Room.databaseBuilder(
            context,
            UserLocalDB::class.java,
            Constants.DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }


    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideUserPager(
        userDatabase: UserLocalDB,
        usersApi: UsersApi,
    ): Pager<Int, User> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 10
            ),
            remoteMediator = UserRemoteMediator(
                database = userDatabase,
                api = usersApi,
            ),
            pagingSourceFactory = { userDatabase.getUserDao().getAll() },
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userPager: Pager<Int, User>,
        userDatabase: UserLocalDB
    ): UserRepository {
        return UserRepositoryImpl(
            userPager = userPager,
            userDatabase = userDatabase
        )
    }
}