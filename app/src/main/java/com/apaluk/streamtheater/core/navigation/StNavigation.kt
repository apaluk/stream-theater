package com.apaluk.streamtheater.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.apaluk.streamtheater.ui.dashboard.DashboardScreen
import com.apaluk.streamtheater.ui.login.LoginScreen
import com.apaluk.streamtheater.ui.media_detail.MediaDetailScreen
import com.apaluk.streamtheater.ui.player.PlayerScreen
import com.apaluk.streamtheater.ui.search.SearchScreen

// TODO separate this navigation to modules

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
    onPlayStream: (String, Long) -> Unit,
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
                onPlayStream = onPlayStream
            )
        }
        composable(
            route = StDestinations.VIDEO_PLAYER_ROUTE,
            arguments = listOf(
                navArgument(StNavArgs.WEBSHARE_IDENT_ARG) { type = NavType.StringType },
                navArgument(StNavArgs.WATCH_HISTORY_ID_ARG) { type = NavType.LongType }
            )
        ) {
            PlayerScreen(
                onNavigateUp = onNavigateUp,
            )
        }
    }
}

fun NavController.navigateToMediaDetail(mediaId: String) {
    navigate(route = "${StScreens.MEDIA_DETAIL_SCREEN}/$mediaId")
}

// TODO change param?
fun NavController.navigateToPlayer(webshareIdent: String, watchHistoryId: Long) {
    navigate(route = "${StScreens.VIDEO_PLAYER_SCREEN}/$webshareIdent/$watchHistoryId")
}