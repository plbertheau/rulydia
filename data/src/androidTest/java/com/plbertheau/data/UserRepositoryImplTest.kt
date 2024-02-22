package com.plbertheau.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plbertheau.data.repository.UserRepositoryImpl
import com.plbertheau.data.room.UserLocalDB
import com.plbertheau.domain.entity.User
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class UserRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var userRepository: UserRepositoryImpl
    private lateinit var mockPager: Pager<Int, User>
    private lateinit var mockUserDatabase: UserLocalDB

    @Before
    fun setUp() {
        mockPager = mock()
        mockUserDatabase = mock()
        userRepository = UserRepositoryImpl(mockPager, mockUserDatabase)
    }

    @Test
    fun testGetUsers() = runBlocking {
        // Given
        val mockUserFlow: Flow<PagingData<User>> =
            flowOf(PagingData.from(listOf(User(0L, name = "tom"), User(1L, name = "jerry"))))
        whenever(mockPager.flow).thenReturn(mockUserFlow)
        // When
        val result: PagingData<User> = userRepository.getUsers().first()
        // Then
        val differ = AsyncPagingDataDiffer(
            diffCallback = TestDiffCallback(),
            updateCallback = TestListCallback(),
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(result)

        val pagingList = differ.snapshot().items //this is the data you wanted
        assertTrue(pagingList.isEmpty().not())
    }

    class TestDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    class TestListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
        }

        override fun onInserted(position: Int, count: Int) {
        }

        override fun onRemoved(position: Int, count: Int) {
        }
    }

}