package com.plbertheau.data.service

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.plbertheau.data.Constants.DEFAULT_PAGE
import com.plbertheau.data.Constants.DEFAULT_SEED
import com.plbertheau.data.mapper.toUser
import com.plbertheau.data.room.UserLocalDB
import com.plbertheau.domain.entity.RemoteKeys
import com.plbertheau.domain.entity.User
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Remote mediator to handle pagination and network requests
 * The Paging library uses the database as a source of truth for the data that needs to be displayed in the UI.
 * Whenever we don't have any more data in the database, we need to request more from the network.
 * To help with this, Paging 3 defines the RemoteMediator abstract class,
 * with one method that needs to be implemented: load().
 * @param database: UserLocalDB - the local database
 * @param api: UsersApi - the remote API
 * This class returns a MediatorResult object, that can either be:
 *
 * Error - if we got an error while requesting data from the network.
 * Success - If we successfully got data from the network.
 *      Here, we also need to pass in a signal that tells whether more data can be loaded or not.
 *      For example, if the network response was successful but we got an empty list of repositories,
 *      it means that there is no more data to be loaded.
 *
 * source - https://developer.android.com/topic/libraries/architecture/paging/v3-network-db
 */
@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator @Inject constructor(
    private val database: UserLocalDB,
    private val api: UsersApi,
) : RemoteMediator<Int, User>() {
    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = false
                )
                nextKey
            }
        }

        try {
            val resultResponse = api.getUsers(page, state.config.pageSize, DEFAULT_SEED)

            val userResponses = resultResponse.items
            val endOfPaginationReached = userResponses.isEmpty()
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    database.getRemoteKeysDao().clearRemoteKeys()
                    database.getUserDao().clear()
                }
                val prevKey = if (page == DEFAULT_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = userResponses.map {
                    RemoteKeys(
                        it.email,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                database.getRemoteKeysDao().insertAll(keys)
                database.getUserDao().insertAll(
                    userResponses.map { it.toUser() }
                )
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, User>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user ->
                // Get the remote keys of the last item retrieved
                database.getRemoteKeysDao().remoteKeysRepoId(user.email)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, User>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.email?.let { email ->
                database.getRemoteKeysDao().remoteKeysRepoId(email)
            }
        }
    }
}