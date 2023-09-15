package com.apaluk.streamtheater.ui.media

import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import com.apaluk.streamtheater.ui.media.media_detail.PlayStreamParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(): BaseViewModel<Unit, MediaEvent, MediaAction>() {

    override val initialState: Unit = Unit

    val playStreamParams = MutableStateFlow<PlayStreamParams?>(null)

    override fun handleAction(action: MediaAction) {
        when(action) {
            MediaAction.SkipToPreviousVideo -> onSkipToPreviousVideo()
            MediaAction.SkipToNextVideo -> onSkipToNextVideo()
        }
    }

    private fun onSkipToPreviousVideo() {
        emitEvent(MediaEvent.SkipToPreviousVideo)
    }

    private fun onSkipToNextVideo() {
        emitEvent(MediaEvent.SkipToNextVideo)
    }
}

sealed class MediaEvent {
    object SkipToPreviousVideo: MediaEvent()
    object SkipToNextVideo: MediaEvent()
}

sealed class MediaAction {
    object SkipToPreviousVideo: MediaAction()
    object SkipToNextVideo: MediaAction()
}

