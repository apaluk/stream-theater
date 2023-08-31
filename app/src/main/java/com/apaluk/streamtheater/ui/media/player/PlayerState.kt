package com.apaluk.streamtheater.ui.media.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerState {
    var wasPlayingBeforeOnPause = false
    val isPlaying = MutableStateFlow(false)
    var isPlayerControlsFullyVisible by mutableStateOf(false)

}