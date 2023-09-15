package com.apaluk.streamtheater.ui.media.player

import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.domain.use_case.media.GetStartFromPositionUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnVideoProgressUseCase
import com.apaluk.streamtheater.domain.use_case.webshare.GetFileLinkUseCase
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import com.apaluk.streamtheater.ui.media.media_detail.PlayStreamParams
import com.apaluk.streamtheater.ui.media.media_detail.util.PlayerMediaInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PlayerViewModel @Inject constructor(
    getFileLink: GetFileLinkUseCase,
    private val updateWatchHistoryOnVideoProgress: UpdateWatchHistoryOnVideoProgressUseCase,
    getStartFromPosition: GetStartFromPositionUseCase,
): BaseViewModel<PlayerScreenUiState, PlayerScreenEvent, PlayerScreenAction>() {

    override val initialState: PlayerScreenUiState = PlayerScreenUiState()

    private val playStreamParams = MutableStateFlow<PlayStreamParams?>(null)

    @OptIn(FlowPreview::class)
    private val currentVideoProgress = MutableSharedFlow<VideoProgress>().apply {
        debounce(500.milliseconds)
            .onEach { videoProgress ->
                playStreamParams.value?.watchHistoryId?.let {
                    updateWatchHistoryOnVideoProgress(it, videoProgress.progress, videoProgress.totalDuration)
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        viewModelScope.launch {
            playStreamParams.filterNotNull().collect { params ->
                emitUiState { it.copy(playerMediaInfo = params.mediaInfo) }

                // get video file link
                getFileLink(params.ident).map { resource ->
                    emitUiState {
                        it.copy(
                            uiState = resource.toUiState(),
                            videoUrl = resource.data,
                        )
                    }
                }.collect()
                // seek video to last watched position
                val startFromPosition = getStartFromPosition(params.watchHistoryId).toLong()
                emitUiState { it.copy(seekToPosition = startFromPosition) }
            }
        }
    }

    fun setParams(params: PlayStreamParams?) {
        if(params != null)
            playStreamParams.value = params
        else
            emitUiState { it.copy(uiState = UiState.Error()) }  // this should not happen
            // TODO log error to firebase
    }

    override fun handleAction(action: PlayerScreenAction) {
        when(action) {
            PlayerScreenAction.VideoEnded -> onVideoEnded()
            is PlayerScreenAction.VideoProgressChanged -> onVideoProgressChanged(action)
            is PlayerScreenAction.PlayerControlsVisibilityChanged -> onPlayerControlsVisibilityChanged(action)
        }
    }

    private fun onVideoEnded() {
        emitEvent(PlayerScreenEvent.NavigateUp)
    }

    private fun onVideoProgressChanged(action: PlayerScreenAction.VideoProgressChanged) {
        viewModelScope.launch {
            currentVideoProgress.emit(action.progress)
        }
    }

    private fun onPlayerControlsVisibilityChanged(action: PlayerScreenAction.PlayerControlsVisibilityChanged) {
        viewModelScope.launch {
            val shouldShowNextPrevButtons = playStreamParams
                .filterNotNull()
                .first()
                .showNextPrevControls && action.visible
            emitUiState { it.copy(showNextPrevButtons = shouldShowNextPrevButtons) }
        }
    }
}

data class PlayerScreenUiState(
    val uiState: UiState = UiState.Loading,
    val videoUrl: String? = null,
    val showNextPrevButtons: Boolean = false,
    val playerMediaInfo: PlayerMediaInfo? = null,
    val seekToPosition: Long? = null,
)

sealed class PlayerScreenAction {
    object VideoEnded: PlayerScreenAction()
    data class VideoProgressChanged(val progress: VideoProgress): PlayerScreenAction()
    data class PlayerControlsVisibilityChanged(val visible: Boolean): PlayerScreenAction()
}

sealed class PlayerScreenEvent {
    object NavigateUp: PlayerScreenEvent()
}

data class VideoProgress(
    val progress: Int,
    val totalDuration: Int
)