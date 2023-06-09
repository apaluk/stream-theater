package com.apaluk.streamtheater.core.navigation

import androidx.navigation.NavController
import com.apaluk.streamtheater.core.navigation.StNavArgs.MEDIA_ID_ARG
import com.apaluk.streamtheater.core.navigation.StNavArgs.WATCH_HISTORY_ID_ARG
import com.apaluk.streamtheater.core.navigation.StNavArgs.WEBSHARE_IDENT_ARG
import com.apaluk.streamtheater.core.navigation.StScreens.DASHBOARD_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.LOGIN_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.MEDIA_DETAIL_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.SEARCH_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.VIDEO_PLAYER_SCREEN

object StScreens {
    const val LOGIN_SCREEN = "login"
    const val DASHBOARD_SCREEN = "dashboard"
    const val SEARCH_SCREEN = "search"
    const val MEDIA_DETAIL_SCREEN = "mediaItem"
    const val VIDEO_PLAYER_SCREEN = "videoPlayer"
}

object StNavArgs {
    const val MEDIA_ID_ARG = "mediaId"
    const val WEBSHARE_IDENT_ARG = "ident"
    const val WATCH_HISTORY_ID_ARG = "watchHistoryId"
}

object StDestinations {
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val DASHBOARD_ROUTE = DASHBOARD_SCREEN
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val MEDIA_DETAIL_ROUTE = "$MEDIA_DETAIL_SCREEN/{$MEDIA_ID_ARG}"
    const val VIDEO_PLAYER_ROUTE = "$VIDEO_PLAYER_SCREEN/{$WEBSHARE_IDENT_ARG}/{$WATCH_HISTORY_ID_ARG}"
}

class StNavActions(
    private val navController: NavController
) {
    fun navigateToDashboard() {
        navController.navigate(route = StDestinations.DASHBOARD_ROUTE) {
            popUpTo(StDestinations.LOGIN_ROUTE) {
                inclusive = true
            }
        }
    }

    fun navigateToSearch() {
        navController.navigate(route = StDestinations.SEARCH_ROUTE)
    }

    fun navigateToPlayer(webshareIdent: String, watchHistoryId: Long) {
        navController.navigate(route = "$VIDEO_PLAYER_SCREEN/$webshareIdent/$watchHistoryId")
    }

    fun navigateToMediaDetail(mediaId: String) {
        navController.navigate(route = "$MEDIA_DETAIL_SCREEN/$mediaId")
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}