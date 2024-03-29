package com.apaluk.streamtheater.core.navigation

import com.apaluk.streamtheater.core.navigation.StNavArgs.MEDIA_ID_ARG
import com.apaluk.streamtheater.core.navigation.StScreens.DASHBOARD_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.LOGIN_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.MEDIA_DETAIL_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.MEDIA_GRAPH
import com.apaluk.streamtheater.core.navigation.StScreens.SEARCH_SCREEN
import com.apaluk.streamtheater.core.navigation.StScreens.VIDEO_PLAYER_SCREEN

object StScreens {
    const val LOGIN_SCREEN = "login"
    const val DASHBOARD_SCREEN = "dashboard"
    const val SEARCH_SCREEN = "search"
    const val MEDIA_DETAIL_SCREEN = "mediaItem"
    const val VIDEO_PLAYER_SCREEN = "videoPlayer"
    const val MEDIA_GRAPH = "media"
}

object StNavArgs {
    const val MEDIA_ID_ARG = "mediaId"
}

object StDestinations {
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val DASHBOARD_ROUTE = DASHBOARD_SCREEN
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val MEDIA_ROUTE = "$MEDIA_GRAPH/{$MEDIA_ID_ARG}"
    const val MEDIA_DETAIL_ROUTE = "$MEDIA_DETAIL_SCREEN/{$MEDIA_ID_ARG}"
    const val VIDEO_PLAYER_ROUTE = VIDEO_PLAYER_SCREEN
}
