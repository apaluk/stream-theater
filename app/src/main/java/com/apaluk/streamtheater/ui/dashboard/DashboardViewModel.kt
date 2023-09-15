package com.apaluk.streamtheater.ui.dashboard

import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.use_case.dashboard.GetContinueWatchingMediaListUseCase
import com.apaluk.streamtheater.domain.use_case.media.RemoveMediaFromHistoryUseCase
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getContinueWatchingMediaList: GetContinueWatchingMediaListUseCase,
    private val removeMediaFromHistory: RemoveMediaFromHistoryUseCase
) : BaseViewModel<DashboardUiState, DashboardEvent, DashboardAction>() {

    override val initialState: DashboardUiState = DashboardUiState()

    init {
        viewModelScope.launch {
            getContinueWatchingMediaList().collect { listResource ->
                emitUiState { it.copy(continueWatchingMediaList = listResource.data) }
                if(listResource !is Resource.Loading && listResource.data.isNullOrEmpty()) {
                    navigateToSearch()
                }
            }
        }
    }

    override fun handleAction(action: DashboardAction) {
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
        emitEvent(DashboardEvent.NavigateToSearch)
    }

    private fun onNavigateToMediaDetail(action: DashboardAction.GoToMediaDetail) {
        action.dashboardMedia.mediaId?.let { mediaId ->
            emitEvent(DashboardEvent.NavigateToMediaDetail(mediaId))
        }
    }

}

data class DashboardUiState(
    val continueWatchingMediaList: List<DashboardMedia>? = null,
)

sealed class DashboardAction {
    data class GoToMediaDetail(val dashboardMedia: DashboardMedia): DashboardAction()
    data class RemoveFromList(val dashboardMedia: DashboardMedia) : DashboardAction()
}

sealed class DashboardEvent {
    data class NavigateToMediaDetail(val mediaId: String) : DashboardEvent()
    object NavigateToSearch : DashboardEvent()
}