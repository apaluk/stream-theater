@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apaluk.streamtheater.ui.search

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.domain.repository.SearchHistoryRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : BaseViewModel<SearchUiState, SearchScreenEvent, SearchScreenAction>() {

    override val initialState: SearchUiState = SearchUiState()

    private var searchJob: MutableStateFlow<Job?> = MutableStateFlow(null)

    init {
        emitEvent(SearchScreenEvent.ShowKeyboard(true))
        fetchSuggestions()
        updateSearchButtonEnabledState()
    }

    override fun handleAction(action: SearchScreenAction) {
        when(action) {
            is SearchScreenAction.SearchTextChanged -> onSearchTextChanged(action)
            is SearchScreenAction.MediaSelected -> onMediaSelected(action)
            SearchScreenAction.TriggerSearch -> onTriggerSearch()
            SearchScreenAction.ClearSearch -> onClearSearch()
            is SearchScreenAction.DeleteSearchHistoryEntry -> onDeleteSearchHistoryEntry(action)
        }
    }

    private fun onMediaSelected(action: SearchScreenAction.MediaSelected) {
        emitEvent(SearchScreenEvent.SelectMedia(action.mediaId))
    }

    private fun onSearchTextChanged(action: SearchScreenAction.SearchTextChanged) {
        emitUiState { it.copy(searchFieldValue = action.textFieldValue) }
    }

    private fun onTriggerSearch() {
        searchJob.value?.cancel()
        val searchText = uiState.value.searchFieldValue.text.trim()
        viewModelScope.launch {
            searchHistoryRepository.addToHistory(searchText)
        }
        searchJob.value = viewModelScope.launch {
            emitEvent(SearchScreenEvent.ShowKeyboard(false))
            emitEvent(SearchScreenEvent.ScrollListToTop)
            emitUiState { it.copy(uiState = UiState.Loading) }
            val searchResource = streamCinemaRepository.search(searchText).last()
            emitUiState {
                it.copy(results = searchResource.data.orEmpty(), uiState = searchResource.toUiState(),)
            }
            searchJob.value = null
        }
    }

    private fun onClearSearch() {
        searchJob.value?.cancel()
        searchJob.value = null
        emitUiState {
            it.copy(
                results = emptyList(),
                uiState = UiState.Idle,
                searchFieldValue = TextFieldValue()
            )
        }
    }

    private fun onDeleteSearchHistoryEntry(action: SearchScreenAction.DeleteSearchHistoryEntry) {
        viewModelScope.launch {
            searchHistoryRepository.deleteFromHistory(action.entry)
        }
    }

    private fun fetchSuggestions() {
        // fetch search suggestions when state is Idle
        viewModelScope.launch {
            combine(
                uiState.map { it.uiState }.distinctUntilChanged(),
                uiState.map { it.searchFieldValue.text }.distinctUntilChanged()
            ) { uiState, searchText ->
                if (uiState == UiState.Idle) searchText else null
            }
                .distinctUntilChanged()
                .flatMapLatest { searchText ->
                    if (searchText != null) {
                        searchHistoryRepository.getFilteredHistory(searchText)
                            .map { list -> list.map { it.text } }
                    } else flowOf(emptyList())
                }.collectLatest { suggestions ->
                    emitUiState { it.copy(suggestions = suggestions) }
                }
        }
    }

    private fun updateSearchButtonEnabledState() {
        viewModelScope.launch {
            combine(
                uiState.map { it.searchFieldValue.text }.distinctUntilChanged(),
                searchJob
            ) { searchText, searchJob ->
                searchText.isNotBlank() && searchJob == null
            }
                .distinctUntilChanged()
                .collect { searchButtonEnabled ->
                    emitUiState { it.copy(searchButtonEnabled = searchButtonEnabled) }
                }
        }
    }

}

data class SearchUiState(
    val uiState: UiState = UiState.Idle,
    val searchFieldValue: TextFieldValue = TextFieldValue(),
    val results: List<SearchResultItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val searchButtonEnabled: Boolean = false
)

sealed class SearchScreenAction {
    data class SearchTextChanged(val textFieldValue: TextFieldValue): SearchScreenAction()
    object TriggerSearch: SearchScreenAction()
    object ClearSearch: SearchScreenAction()
    data class MediaSelected(val mediaId: String): SearchScreenAction()
    data class DeleteSearchHistoryEntry(val entry: String): SearchScreenAction()
}

sealed class SearchScreenEvent {
    data class ShowKeyboard(val show: Boolean): SearchScreenEvent()
    data class SelectMedia(val mediaId: String): SearchScreenEvent()
    object ScrollListToTop: SearchScreenEvent()
}