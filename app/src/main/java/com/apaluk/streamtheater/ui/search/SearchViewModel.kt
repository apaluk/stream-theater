@file:OptIn(ExperimentalCoroutinesApi::class)

package com.apaluk.streamtheater.ui.search

import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.domain.repository.SearchHistoryRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.toUiState
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : BaseViewModel<SearchUiState, SearchScreenEvent, SearchScreenAction>() {

    override val initialState: SearchUiState = SearchUiState()

    init {
        emitEvent(SearchScreenEvent.ShowKeyboard(true))
        // fetch search suggestions when in Idle state
        viewModelScope.launch {
            combine(
                uiState.map { it.uiState }.distinctUntilChanged(),
                uiState.map { it.searchInput }.distinctUntilChanged()
            ) { uiState, searchText ->
                if (uiState == UiState.Idle)
                    searchText
                else
                    null
            }.flatMapLatest { searchText ->
                if (searchText != null) {
                    searchHistoryRepository
                        .getFilteredHistory(searchText.text)
                        .map { list ->
                            list.map { it.text }
                        }

                } else flowOf(null)
            }.collectLatest { suggestions ->
                emitUiState { it.copy(searchSuggestions = suggestions) }
            }
        }
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
        emitUiState { it.copy(searchInput = TextFieldInput(action.text, action.cursorPosition)) }
        if(action.triggerSearch) {
            onTriggerSearch()
        }
    }

    private fun onTriggerSearch() {
        val searchText = uiState.value.searchInput.text.trim()
        viewModelScope.launch {
            searchHistoryRepository.addToHistory(searchText)
        }
        viewModelScope.launch {
            emitEvent(SearchScreenEvent.ShowKeyboard(false))
            emitEvent(SearchScreenEvent.ScrollListToTop)
            emitUiState { it.copy(uiState = UiState.Loading) }
            val searchResource = streamCinemaRepository.search(searchText).last()
            emitUiState {
                it.copy(searchResults = searchResource.data.orEmpty(), uiState = searchResource.toUiState(),)
            }
        }
    }

    private fun onClearSearch() {
        emitUiState {
            it.copy(
                searchResults = emptyList(),
                uiState = UiState.Idle,
                searchInput = TextFieldInput()
            )
        }
    }

    private fun onDeleteSearchHistoryEntry(action: SearchScreenAction.DeleteSearchHistoryEntry) {
        viewModelScope.launch {
            searchHistoryRepository.deleteFromHistory(action.entry)
        }
    }
}

data class SearchUiState(
    val searchInput: TextFieldInput = TextFieldInput(),
    val searchResults: List<SearchResultItem> = emptyList(),
    val uiState: UiState = UiState.Idle,
    val searchSuggestions: List<String>? = null,
)

sealed class SearchScreenAction {
    data class SearchTextChanged(
        val text: String,
        val cursorPosition: Int,
        val triggerSearch: Boolean = false
    ): SearchScreenAction()
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

data class TextFieldInput(
    val text: String = "",
    val cursorPosition: Int = 0,
)
