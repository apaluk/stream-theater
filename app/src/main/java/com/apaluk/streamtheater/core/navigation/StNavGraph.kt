package com.apaluk.streamtheater.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.apaluk.streamtheater.core.navigation.StDestinations.DASHBOARD_ROUTE
import com.apaluk.streamtheater.core.navigation.StDestinations.LOGIN_ROUTE
import com.apaluk.streamtheater.core.navigation.StDestinations.MEDIA_DETAIL_ROUTE
import com.apaluk.streamtheater.core.navigation.StDestinations.SEARCH_ROUTE
import com.apaluk.streamtheater.core.navigation.StDestinations.VIDEO_PLAYER_ROUTE
import com.apaluk.streamtheater.core.navigation.StNavArgs.MEDIA_ID_ARG
import com.apaluk.streamtheater.core.navigation.StNavArgs.WATCH_HISTORY_ID_ARG
import com.apaluk.streamtheater.core.navigation.StNavArgs.WEBSHARE_IDENT_ARG
import com.apaluk.streamtheater.ui.dashboard.DashboardScreen
import com.apaluk.streamtheater.ui.login.LoginScreen
import com.apaluk.streamtheater.ui.media_detail.MediaDetailScreen
import com.apaluk.streamtheater.ui.player.PlayerScreen
import com.apaluk.streamtheater.ui.search.SearchScreen

@Composable
fun StNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LOGIN_ROUTE,
        modifier = modifier
    ) {
        loginScreen(
            onLoggedIn = navController::navigateToDashboard
        )
        dashboardScreen(
            onNavigateToSearch = navController::navigateToSearch,
            onNavigateToMediaDetail = navController::navigateToMediaDetail
        )
        searchScreen(
            onNavigateUp = navController::navigateUp,
            onNavigateToMediaDetail = navController::navigateToMediaDetail
        )
        mediaGraph(
            onNavigateUp = navController::navigateUp,
            onPlayStream = navController::navigateToPlayer
        )
    }
}