package com.apaluk.streamtheater.ui.media.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class PlayerState {
    var wasPlayingBeforeOnPause by mutableStateOf(false)
    var isPlayerControlsFullyVisible by mutableStateOf(false)
}

@Composable
fun rememberPlayerState(): PlayerState {
    return remember { PlayerState() }
}