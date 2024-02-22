package com.plbertheau.data


import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plbertheau.data.room.UserDao
import com.plbertheau.data.room.UserLocalDB
import com.plbertheau.domain.entity.User
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var sut: UserDao
    private lateinit var db: UserLocalDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, UserLocalDB::class.java
        ).build()
        sut = db.getUserDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertOneShouldCreateOnlyNewUser() = runBlocking {
        val user = User(name = FAKE_NAME, email = FAKE_EMAIL)

        sut.insertAll(listOf(user))

        val data = sut.getAll().load(PagingSource.LoadParams.Refresh(12, 12, false))
        val result = (data as? PagingSource.LoadResult.Page)?.data?.first()
        assertTrue(result?.email == FAKE_EMAIL)
        assertTrue(result?.name == FAKE_NAME)
    }

    @Test
    @Throws(Exception::class)
    fun insertAllShouldCreateAllNewUser() = runBlocking {
        val user = User(name = FAKE_NAME, email = FAKE_EMAIL)
        val user2 = User(name = FAKE_EMAIL, email = FAKE_NAME)

        sut.insertAll(listOf(user, user2))

        val data = sut.getAll().load(PagingSource.LoadParams.Refresh(12, 12, false))
        val result = (data as? PagingSource.LoadResult.Page)?.data
        assertTrue(result?.size == 2)
        assertTrue(result?.getOrNull(0)?.name == FAKE_NAME)
        assertTrue(result?.getOrNull(0)?.email == FAKE_EMAIL)
        assertTrue(result?.getOrNull(1)?.name == FAKE_EMAIL)
        assertTrue(result?.getOrNull(1)?.email == FAKE_NAME)
    }

    @Test
    @Throws(Exception::class)
    fun clearShouldDeleteAllUser() = runBlocking {
        val user = User(name = FAKE_NAME, email = FAKE_EMAIL)
        sut.insertAll(listOf(user))

        sut.clear()

        val data = sut.getAll().load(PagingSource.LoadParams.Refresh(FAKE_INT, FAKE_INT, false))
        val result = (data as? PagingSource.LoadResult.Page)?.data
        assertTrue(result?.isEmpty()?:false)
    }


    companion object {
        const val FAKE_INT= 43
        const val FAKE_NAME = "FAKE_NAME"
        const val FAKE_EMAIL = "FAKE_EMAIL"
    }
}