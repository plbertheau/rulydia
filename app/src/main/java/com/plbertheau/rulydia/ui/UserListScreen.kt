package com.plbertheau.rulydia.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.plbertheau.domain.entity.User
import com.plbertheau.rulydia.viewmodel.UserListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun UserListScreen(
    navigateToDetail: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val viewModel = hiltViewModel<UserListViewModel>()
    val userPagingItems = viewModel.userPagingDataFlow.collectAsLazyPagingItems()

    if (userPagingItems.loadState.refresh is LoadState.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(
                withDismissAction = true,
                message = (userPagingItems.loadState.refresh as LoadState.Error).error.message ?: ""
            )
        }
    }
    UserListContent(
        pagingItems = userPagingItems,
        navigateToDetail = navigateToDetail,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserListContent(
    pagingItems: LazyPagingItems<User>,
    navigateToDetail: (String) -> Unit,
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableIntStateOf(20) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    val rotation = animateFloatAsState(state.progress * 120)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        if (pagingItems.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!refreshing) {
                    items(
                        count = pagingItems.itemCount,
                    ) { index ->
                        val user = pagingItems[index]
                        if (user != null) {
                            UserItem(
                                user = user,
                                onClick = {
                                    navigateToDetail(user.email)
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
                item {
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopCenter)
                    .pullRefreshIndicatorTransform(state)
                    .rotate(rotation.value),
                shape = RoundedCornerShape(20.dp),
                color = Color.DarkGray,
                elevation = if (state.progress > 0 || refreshing) 20.dp else 0.dp,
            ) {
                Box {
                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(25.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = modifier
                .clickable { onClick() }
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = user.imageThumbnail,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            Text(
                "${user.name}",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null,
            )
        }
    }
}