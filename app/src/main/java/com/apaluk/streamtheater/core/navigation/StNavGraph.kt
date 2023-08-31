package com.apaluk.streamtheater.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.apaluk.streamtheater.core.navigation.StDestinations.LOGIN_ROUTE

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
            onPlayStream = navController::navigateToPlayer,
            navController = navController
        )
    }
}