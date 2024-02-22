package com.plbertheau.rulydia.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val title = currentBackStackEntry?.destination?.label.toString()
                val showBackButton = navController.previousBackStackEntry != null

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            modifier = Modifier,
                            title = {
                                Text(title)
                            },
                            navigationIcon = {
                                if (showBackButton) {
                                    IconButton(onClick = {
                                        navController.popBackStack()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                        )
                    }
                ) { padding ->
                    val speedAnimation = 1000
                    NavHost(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(speedAnimation)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(speedAnimation)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(speedAnimation)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(speedAnimation)
                            )
                        },
                        startDestination = "user-list",
                    ) {
                        composable(
                            "user-list",
                            label = "User list",
                        ) {
                            UserListScreen(
                                navigateToDetail = { email ->
                                    navController.navigate("user/$email")
                                },
                                snackbarHostState = snackbarHostState
                            )
                        }
                        composable(
                            "user/{email}",
                            label = "User",
                            arguments = listOf(navArgument("email") { type = NavType.StringType }),
                        ) {
                            UserDetailScreen(snackbarHostState = snackbarHostState)
                        }
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.composable(
    route: String,
    label: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    addDestination(
        ComposeNavigator.Destination(
            provider[ComposeNavigator::class],
            content
        ).apply {
            this.route = route
            this.label = label // SET LABEL
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
        }
    )
}