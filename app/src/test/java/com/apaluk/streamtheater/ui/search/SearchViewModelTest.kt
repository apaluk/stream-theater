package com.apaluk.streamtheater.ui.search

import com.apaluk.streamtheater.core.testing.SearchHistoryRepositoryFake
import com.apaluk.streamtheater.core.testing.StreamCinemaRepositoryFake
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.ui.common.util.UiState
import com.google.common.truth.Truth.assertThat
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

    @Test
    fun `successful search updates the ui state`() = runTest {
        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Idle)
        viewModel.onSearchScreenAction(SearchScreenAction.SearchTextChanged("ac"))
        assertThat(viewModel.uiState.value.searchText).isEqualTo("ac")
        viewModel.onSearchScreenAction(SearchScreenAction.TriggerSearch)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Content)
        assertThat(viewModel.uiState.value.searchResults).isNotEmpty()
        assertThat(viewModel.uiState.value.scrollListToTop).isFalse()
        assertThat(viewModel.uiState.value.errorToast).isNull()
    }

    @Test
    fun `clearing search removes the content`() = runTest {
        viewModel.onSearchScreenAction(SearchScreenAction.SearchTextChanged("ac"))
        viewModel.onSearchScreenAction(SearchScreenAction.TriggerSearch)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Content)
        viewModel.onSearchScreenAction(SearchScreenAction.ClearSearch)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Idle)
        assertThat(viewModel.uiState.value.searchText).isEmpty()
        assertThat(viewModel.uiState.value.searchResults).isEmpty()
    }

    @Test
    fun `selecting media updates the ui state`() = runTest {
        assertThat(viewModel.uiState.value.selectedMediaId).isNull()
        viewModel.onSearchScreenAction(SearchScreenAction.MediaSelected("x"))
        assertThat(viewModel.uiState.value.selectedMediaId).isEqualTo("x")
        viewModel.onSearchScreenAction(SearchScreenAction.MediaSelected(null))
        assertThat(viewModel.uiState.value.selectedMediaId).isNull()
    }

    @Test
    fun `unsuccessful search sets empty ui state`() = runTest {
        viewModel.onSearchScreenAction(SearchScreenAction.SearchTextChanged("xxxxx"))
        viewModel.onSearchScreenAction(SearchScreenAction.TriggerSearch)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.uiState).isEqualTo(UiState.Empty)
    }
}