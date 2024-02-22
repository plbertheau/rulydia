package com.plbertheau.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.repository.UserRepository
import com.plbertheau.domain.usecase.GetUserListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetUserListUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `invoke_should_return_user_paging_data`() = runTest {
        Dispatchers.setMain(testDispatcher)
        // Given
        val users = listOf(User(id = 0L, name = "User1"), User(id = 1L, name = "User2"))
        val expectedPagingData: PagingData<User> = PagingData.from(users)
        val mockRepository: UserRepository = mock()
        val useCase = GetUserListUseCase(mockRepository)
        val mockFlow: Flow<PagingData<User>> = flowOf(expectedPagingData)
        whenever(mockRepository.getUsers()).thenReturn(mockFlow)

        // When
        val result: PagingData<User> = useCase.invoke().first()
        val differ = AsyncPagingDataDiffer(
            diffCallback = TestDiffCallback(),
            updateCallback = TestListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(result)
        val pagingList = differ.snapshot().items
        // Then
        assertEquals(pagingList, users)
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