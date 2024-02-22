package com.plbertheau.rulydia.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.plbertheau.domain.entity.Response
import com.plbertheau.domain.model.UserResponse
import com.plbertheau.rulydia.viewmodel.UserDetailViewModel

@Composable
fun UserDetailScreen(snackbarHostState: SnackbarHostState) {
    val viewModel = hiltViewModel<UserDetailViewModel>()
    val userResponse by viewModel.userResponse.collectAsStateWithLifecycle()

    if (userResponse is Response.Error) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar((userResponse as Response.Error).error)
        }
    }

    UserDetailContent(userResponse = userResponse)
}

@Composable
fun UserDetailContent(userResponse: Response<UserResponse>) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (userResponse.data != null) {
            val user = userResponse.data!!
            AsyncImage(
                model = user.picture?.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "${user.nom?.first} ${user.nom?.last}",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    user.phone,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "${user.locationResponse?.city} ${user.locationResponse?.state}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        if (userResponse is Response.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}