package com.plbertheau.rulydia

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plbertheau.domain.entity.User
import com.plbertheau.domain.usecase.GetUserListUseCase
import com.plbertheau.rulydia.viewmodel.UserListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class UserListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `getUsers_should_return_the_same_data_as_repository_getUser`() = runTest {
        // Given
        val users = listOf(User(id = 0L, name = "User1"), User(id = 1L, name = "User2"))
        val expectedPagingData: PagingData<User> = PagingData.from(users)
        val mockUseCase = mock<GetUserListUseCase>()
        val mockFlow: Flow<PagingData<User>> = flowOf(expectedPagingData)
        whenever(mockUseCase.invoke()).thenReturn(mockFlow)
        val viewModel = UserListViewModel(mockUseCase)
        // When
        val result: PagingData<User> = viewModel.userPagingDataFlow.first()
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

    @Test
    fun `userPagingDataFlow_is_initialized_with_non-null_value`() =
        runTest {
            val users = listOf(User(id = 0L, name = "User1"), User(id = 1L, name = "User2"))
            val expectedPagingData: PagingData<User> = PagingData.from(users)
            val mockUseCase = mock<GetUserListUseCase>()
            val mockFlow: Flow<PagingData<User>> = flowOf(expectedPagingData)
            whenever(mockUseCase.invoke()).thenReturn(mockFlow)
            val viewModel = UserListViewModel(mockUseCase)
            // When
            val result: PagingData<User> = viewModel.userPagingDataFlow.first()
            val differ = AsyncPagingDataDiffer(
                diffCallback = TestDiffCallback(),
                updateCallback = TestListCallback(),
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(result)
            val pagingList = differ.snapshot().items

            // Then
            assertNotNull(pagingList)

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
