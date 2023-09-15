package com.apaluk.streamtheater.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.SingleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<State : Any, Event : Any, Action : Any> : ViewModel() {

    protected abstract val initialState: State

    private val _uiState: MutableStateFlow<State> by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<State>
        get() = _uiState

    private val _event = SingleEvent<Event>()
    val event = _event.flow

    fun onAction(action: Action) {
        Timber.tag(this::class.simpleName ?: "").d("Received action: $action")
        handleAction(action)
    }

    protected fun emitEvent(event: Event) {
        Timber.tag(this::class.simpleName ?: "").d("Emit event: $event")
        viewModelScope.launch {
            _event.emit(event)
        }
    }

    protected fun emitUiState(state: State) {
        Timber.tag(this::class.simpleName ?: "").d("Emit state: $state")
        _uiState.value = state
    }

    protected fun emitUiState(newStateProducer: (State) -> State) {
        emitUiState(newStateProducer(uiState.value))
    }

    protected abstract fun handleAction(action: Action)
}