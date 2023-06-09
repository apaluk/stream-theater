@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apaluk.streamtheater.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.domain.repository.SearchHistoryRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value.showKeyboardEvent.emit(true)
        }
        // fetch search suggestions when in Idle state
        viewModelScope.launch {
            combine(
                _uiState.map { it.uiState }.distinctUntilChanged(),
                _uiState.map { it.searchText }.distinctUntilChanged()
            ) { uiState, searchText ->
                if (uiState == UiState.Idle)
                    searchText
                else
                    null
            }.flatMapLatest { searchText ->
                if (searchText != null) {
                    searchHistoryRepository
                        .getFilteredHistory(searchText)
                        .map { list ->
                            list.map { it.text }
                        }

                } else flowOf(null)
            }.collectLatest {  suggestions ->
                _uiState.update {
                    it.copy(searchSuggestions = suggestions)
                }
            }
        }
    }

    fun onSearchScreenAction(action: SearchScreenAction) {
        when(action) {
            is SearchScreenAction.SearchTextChanged -> onSearchTextChanged(action)
            is SearchScreenAction.MediaSelected ->
                if(action.mediaId == null) clearSelectedMedia() else onMediaSelected(action.mediaId)
            SearchScreenAction.TriggerSearch -> triggerSearch()
            SearchScreenAction.ClearSearch -> clearSearch()
            is SearchScreenAction.DeleteSearchHistoryEntry -> deleteSearchHistoryEntry(action.entry)
        }
    }

    private fun onMediaSelected(mediaId: String) {
        _uiState.update { it.copy(selectedMediaId = mediaId) }
    }

    private fun clearSelectedMedia() {
        _uiState.update { it.copy(selectedMediaId = null) }
    }

    private fun onSearchTextChanged(action: SearchScreenAction.SearchTextChanged) {
        var text = action.text
        if(action.moveSearchCursorToEnd) {
            if(text.isNotEmpty()) {
                text = "${text.trim()} "
            }
            viewModelScope.launch {
                _uiState.value.moveSearchFieldCursorToTheEndEvent.emit(text)
            }
        }
        _uiState.update { it.copy(searchText = text) }

        if(action.triggerSearch) {
            triggerSearch()
        }
    }

    private fun triggerSearch() {
        val searchText = _uiState.value.searchText.trim()
        viewModelScope.launch {
            searchHistoryRepository.addToHistory(searchText)
        }
        viewModelScope.launch {
            _uiState.value.showKeyboardEvent.emit(false)
            _uiState.update {
                it.copy(
                    uiState = UiState.Loading,
                    scrollListToTop = true
                )
            }
            val searchResource = streamCinemaRepository.search(searchText).last()
            _uiState.update { it.copy(
                searchResults = searchResource.data.orEmpty(),
                uiState = searchResource.toUiState(),
                scrollListToTop = false
            ) }
        }
    }

    private fun clearSearch() {
        viewModelScope.launch {
            _uiState.value.moveSearchFieldCursorToTheEndEvent.emit("")
        }
        _uiState.update { it.copy(
            searchResults = emptyList(),
            uiState = UiState.Idle,
            searchText = ""
        ) }
    }

    private fun deleteSearchHistoryEntry(entry: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteFromHistory(entry)
        }
    }
}

data class SearchUiState(
    val selectedMediaId: String? = null,
    val searchText: String = "",
    val errorToast: String? = null,
    val searchResults: List<SearchResultItem> = emptyList(),
    val uiState: UiState = UiState.Idle,
    val scrollListToTop: Boolean = false,
    val showKeyboardEvent: SingleEvent<Boolean> = SingleEvent(),
    val searchSuggestions: List<String>? = null,
    val moveSearchFieldCursorToTheEndEvent: SingleEvent<String> = SingleEvent()
)

sealed class SearchScreenAction {
    data class SearchTextChanged(
        val text: String,
        val moveSearchCursorToEnd: Boolean = false,
        val triggerSearch: Boolean = false
    ): SearchScreenAction()
    object TriggerSearch: SearchScreenAction()
    object ClearSearch: SearchScreenAction()
    data class MediaSelected(val mediaId: String?): SearchScreenAction()
    data class DeleteSearchHistoryEntry(val entry: String): SearchScreenAction()
}
