package com.apaluk.streamtheater.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.use_case.dashboard.GetContinueWatchingMediaListUseCase
import com.apaluk.streamtheater.domain.use_case.media.RemoveMediaFromHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getContinueWatchingMediaList: GetContinueWatchingMediaListUseCase,
    private val removeMediaFromHistory: RemoveMediaFromHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getContinueWatchingMediaList().collect { listResource ->
                _uiState.value = _uiState.value.copy(continueWatchingMediaList = listResource.data)
                if(listResource !is Resource.Loading && listResource.data.isNullOrEmpty()) {
                    navigateToSearch()
                }
            }
        }
    }

    fun onDashboardAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.GoToMediaDetail -> onNavigateToMediaDetail(action)
            is DashboardAction.RemoveFromList -> onRemoveFromList(action)
        }
    }

    private fun onRemoveFromList(action: DashboardAction.RemoveFromList) {
        viewModelScope.launch {
            removeMediaFromHistory(action.dashboardMedia)
        }
    }

    private fun navigateToSearch() {
        viewModelScope.launch {
            _uiState.value.navigateToSearchEvent.emit(Unit)
        }
    }

    private fun onNavigateToMediaDetail(action: DashboardAction.GoToMediaDetail) {
        viewModelScope.launch {
            action.dashboardMedia.mediaId?.let {
                _uiState.value.navigateToMediaDetailEvent.emit(it)
            }
        }
    }
}

data class DashboardUiState(
    val continueWatchingMediaList: List<DashboardMedia>? = null,
    val navigateToMediaDetailEvent: SingleEvent<String> = SingleEvent(),
    val navigateToSearchEvent: SingleEvent<Unit> = SingleEvent()
)

sealed class DashboardAction {
    data class GoToMediaDetail(val dashboardMedia: DashboardMedia): DashboardAction()
    data class RemoveFromList(val dashboardMedia: DashboardMedia) : DashboardAction() {

    }
}