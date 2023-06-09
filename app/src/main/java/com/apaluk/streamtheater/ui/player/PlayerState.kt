package com.apaluk.streamtheater.ui.player

import kotlinx.coroutines.flow.MutableStateFlow

class PlayerState {
    var wasPlayingBeforeOnPause = false
    val isPlaying = MutableStateFlow(false)
}