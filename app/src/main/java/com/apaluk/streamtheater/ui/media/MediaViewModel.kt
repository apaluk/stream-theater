package com.apaluk.streamtheater.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.ui.media.media_detail.PlayStreamParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(

): ViewModel() {

    val playStreamParams = MutableStateFlow<PlayStreamParams?>(null)

    val skipToPreviousVideoEvent = SingleEvent<Unit>()
    val skipToNextVideoEvent = SingleEvent<Unit>()

    fun skipToPreviousVideo() {
        viewModelScope.launch { skipToPreviousVideoEvent.emit(Unit) }
    }
    fun skipToNextVideo() {
        viewModelScope.launch { skipToNextVideoEvent.emit(Unit) }
    }

}

