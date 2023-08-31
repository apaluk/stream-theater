package com.apaluk.streamtheater.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.apaluk.streamtheater.ui.dashboard.DashboardScreen
import com.apaluk.streamtheater.ui.login.LoginScreen
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailScreen
import com.apaluk.streamtheater.ui.media.player.PlayerScreen
import com.apaluk.streamtheater.ui.search.SearchScreen

// login screen
fun NavGraphBuilder.loginScreen(
    onLoggedIn: () -> Unit
) {
    composable(
        route = StDestinations.LOGIN_ROUTE
    ) {
        LoginScreen(
            onLoggedIn = onLoggedIn
        )
    }
}

// dashboard screen
fun NavGraphBuilder.dashboardScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToMediaDetail: (String) -> Unit,
) {
    composable(
        route = StDestinations.DASHBOARD_ROUTE
    ) {
        DashboardScreen(
            onNavigateToSearch = onNavigateToSearch,
            onNavigateToMediaDetail = onNavigateToMediaDetail
        )
    }
}

fun NavController.navigateToDashboard() {
    navigate(route = StDestinations.DASHBOARD_ROUTE) {
        popUpTo(StDestinations.LOGIN_ROUTE) {
            inclusive = true
        }
    }
}

// search screen
fun NavGraphBuilder.searchScreen(
    onNavigateUp: () -> Unit,
    onNavigateToMediaDetail: (String) -> Unit
) {
    composable(
        route = StDestinations.SEARCH_ROUTE
    ) {
        SearchScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToMediaDetail = onNavigateToMediaDetail
        )
    }
}

fun NavController.navigateToSearch() {
    navigate(route = StDestinations.SEARCH_ROUTE)
}

// media
fun NavGraphBuilder.mediaGraph(
    onNavigateUp: () -> Unit,
    onPlayStream: () -> Unit,
    navController: NavController
) {
    navigation(
        route = StDestinations.MEDIA_ROUTE,
        startDestination = StScreens.MEDIA_DETAIL_SCREEN
    ) {
        
        composable(
            route = StDestinations.MEDIA_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(StNavArgs.MEDIA_ID_ARG) { type = NavType.StringType }
            )
        ) {
            MediaDetailScreen(
                onNavigateUp = onNavigateUp,
                onPlayStream = onPlayStream,
                mediaViewModel = it.sharedViewModel(navController)
            )
        }
        composable(
            route = StDestinations.VIDEO_PLAYER_ROUTE
        ) {
            PlayerScreen(
                onNavigateUp = onNavigateUp,
                mediaViewModel = it.sharedViewModel(navController)
            )
        }
    }
}

fun NavController.navigateToMediaDetail(mediaId: String) {
    navigate(route = "${StScreens.MEDIA_DETAIL_SCREEN}/$mediaId")
}

fun NavController.navigateToPlayer() {
    navigate(route = StScreens.VIDEO_PLAYER_SCREEN)
}