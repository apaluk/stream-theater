package com.apaluk.streamtheater.ui.media.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.use_case.media.GetStartFromPositionUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnVideoProgressUseCase
import com.apaluk.streamtheater.domain.use_case.webshare.GetFileLinkUseCase
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.media.media_detail.PlayStreamParams
import com.apaluk.streamtheater.ui.media.media_detail.util.PlayerMediaInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class PlayerViewModel @Inject constructor(
    getFileLink: GetFileLinkUseCase,
    private val updateWatchHistoryOnVideoProgress: UpdateWatchHistoryOnVideoProgressUseCase,
    getStartFromPosition: GetStartFromPositionUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(PlayerScreenState())
    val uiState = _uiState.asStateFlow()

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
                _uiState.update { it.copy(playerMediaInfo = params.mediaInfo) }

                // get video file link
                getFileLink(params.ident).map { resource ->
                    _uiState.update {
                        it.copy(
                            uiState = resource.toUiState(),
                            videoUrl = resource.data,
                        )
                    }
                }.collect()
                // seek video to last watched position
                _uiState.value.startFromPositionEvent.emit(getStartFromPosition(params.watchHistoryId))
            }
        }
    }

    fun setParams(params: PlayStreamParams?) {
        if(params != null)
            playStreamParams.value = params
        else
            _uiState.update { it.copy(uiState = UiState.Error()) }  // this should not happen
            // TODO log error to firebase
    }

    fun onPlayerScreenAction(action: PlayerScreenAction) {
        when(action) {
            PlayerScreenAction.VideoEnded -> onVideoEnded()
            is PlayerScreenAction.VideoProgressChanged -> onVideoProgressChanged(action)
            is PlayerScreenAction.PlayerControlsVisibilityChanged -> onPlayerControlsVisibilityChanged(action)
        }
    }

    private fun onVideoEnded() {
        viewModelScope.launch {
            _uiState.value.navigateUpEvent.emit(Unit)
        }
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
            _uiState.update { it.copy(showNextPrevButtons = shouldShowNextPrevButtons) }
        }
    }
}

data class PlayerScreenState(
    val uiState: UiState = UiState.Loading,
    val videoUrl: String? = null,
    val showNextPrevButtons: Boolean = false,
    val playerMediaInfo: PlayerMediaInfo? = null,
    val navigateUpEvent: SingleEvent<Unit> = SingleEvent(),
    val startFromPositionEvent: SingleEvent<Int> = SingleEvent()
)

sealed class PlayerScreenAction {
    object VideoEnded: PlayerScreenAction()
    data class VideoProgressChanged(val progress: VideoProgress): PlayerScreenAction()
    data class PlayerControlsVisibilityChanged(val visible: Boolean): PlayerScreenAction()
}

data class VideoProgress(
    val progress: Int,
    val totalDuration: Int
)