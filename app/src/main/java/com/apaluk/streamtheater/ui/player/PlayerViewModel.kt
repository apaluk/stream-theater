package com.apaluk.streamtheater.ui.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.navigation.StNavArgs
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.use_case.media.GetStartFromPositionUseCase
import com.apaluk.streamtheater.domain.use_case.media.UpdateWatchHistoryOnVideoProgressUseCase
import com.apaluk.streamtheater.domain.use_case.webshare.GetFileLinkUseCase
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    getFileLink: GetFileLinkUseCase,
    private val updateWatchHistoryOnVideoProgress: UpdateWatchHistoryOnVideoProgressUseCase,
    getStartFromPosition: GetStartFromPositionUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val webshareIdent: String = requireNotNull(savedStateHandle[StNavArgs.WEBSHARE_IDENT_ARG])
    private val watchHistoryId: Long = requireNotNull(savedStateHandle[StNavArgs.WATCH_HISTORY_ID_ARG])

    private val _uiState = MutableStateFlow(PlayerScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        // get video file link
        viewModelScope.launch {
            getFileLink(webshareIdent).map { resource ->
                _uiState.update { it.copy(
                    uiState = resource.toUiState(),
                    videoUrl = resource.data
                ) }
            }.collect()
        }
        viewModelScope.launch {
            _uiState.value.startFromPositionEvent.emit(getStartFromPosition(watchHistoryId))
        }
    }

    fun onPlayerScreenAction(action: PlayerScreenAction) {
        when(action) {
            PlayerScreenAction.VideoEnded -> onVideoEnded()
            is PlayerScreenAction.VideoProgressChanged -> onVideoProgressChanged(action)
        }
    }

    private fun onVideoEnded() {
        viewModelScope.launch {
            _uiState.value.navigateUpEvent.emit(Unit)
        }
    }

    private fun onVideoProgressChanged(action: PlayerScreenAction.VideoProgressChanged) {
        viewModelScope.launch {
            updateWatchHistoryOnVideoProgress(watchHistoryId, action.progress, action.totalDuration)
        }
    }
}

data class PlayerScreenState(
    val uiState: UiState = UiState.Loading,
    val videoUrl: String? = null,
    val navigateUpEvent: SingleEvent<Unit> = SingleEvent(),
    val startFromPositionEvent: SingleEvent<Int> = SingleEvent()
)

sealed class PlayerScreenAction {
    object VideoEnded: PlayerScreenAction()
    data class VideoProgressChanged(
        val progress: Int,
        val totalDuration: Int
    ): PlayerScreenAction()
}