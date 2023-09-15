package com.apaluk.streamtheater.ui.search

import com.apaluk.streamtheater.core.testing.SearchHistoryRepositoryFake
import com.apaluk.streamtheater.core.testing.StreamCinemaRepositoryFake
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(
            StreamCinemaRepositoryFake(),
            SearchHistoryRepositoryFake()
        )
    }

    // TODO
    @Test
    fun `successful search updates the ui state`() = runTest {
//        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Idle)
//        viewModel.onAction(SearchScreenAction.SearchTextChanged("ac"))
//        assertThat(viewModel.uiState.value.searchInput).isEqualTo("ac")
//        viewModel.onAction(SearchScreenAction.TriggerSearch)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Content)
//        assertThat(viewModel.uiState.value.searchResults).isNotEmpty()
    }

    // TODO
    @Test
    fun `clearing search removes the content`() = runTest {
//        viewModel.onAction(SearchScreenAction.SearchTextChanged("ac"))
//        viewModel.onAction(SearchScreenAction.TriggerSearch)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Content)
//        viewModel.onAction(SearchScreenAction.ClearSearch)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Idle)
//        assertThat(viewModel.uiState.value.searchInput.text).isEmpty()
//        assertThat(viewModel.uiState.value.searchInput.moveCursorToEnd).isFalse()
//        assertThat(viewModel.uiState.value.searchResults).isEmpty()
    }

    // TODO
    @Test
    fun `selecting media updates the ui state`() = runTest {
//        assertThat(viewModel.uiState.value.selectedMediaId).isNull()
//        viewModel.onAction(SearchScreenAction.MediaSelected("x"))
//        assertThat(viewModel.uiState.value.selectedMediaId).isEqualTo("x")
//        viewModel.onAction(SearchScreenAction.MediaSelected(null))
//        assertThat(viewModel.uiState.value.selectedMediaId).isNull()
    }

    // TODO
    @Test
    fun `unsuccessful search sets empty ui state`() = runTest {
//        viewModel.onAction(SearchScreenAction.SearchTextChanged("xxxxx"))
//        viewModel.onAction(SearchScreenAction.TriggerSearch)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Empty)
    }
}