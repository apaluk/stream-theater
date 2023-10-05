package com.apaluk.streamtheater.ui.search

import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.search.SearchHistoryItem
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.domain.repository.SearchHistoryRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import com.apaluk.streamtheater.ui.common.util.UiState
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel

    @MockK
    private lateinit var searchHistoryRepository: SearchHistoryRepository

    @MockK
    private lateinit var streamCinemaRepository: StreamCinemaRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { searchHistoryRepository.getFilteredHistory(eq("")) } returns flowOf(listOf(SearchHistoryItem("", Date())))
        every { searchHistoryRepository.getFilteredHistory(eq("1")) } returns flowOf(emptyList())
        coEvery { searchHistoryRepository.addToHistory(any()) } just Runs
        every { streamCinemaRepository.search("1") } returns flowOf(
            Resource.Success(listOf(SearchResultItem("", "", "", "", "", null)))
        )
        viewModel = SearchViewModel(streamCinemaRepository, searchHistoryRepository)
    }

    @Test
    fun `when initialized, keyboard is displayed and suggestions are fetched`() = runTest {
        viewModel.event.test {
            assertThat(awaitItem()).isEqualTo(SearchScreenEvent.ShowKeyboard(true))
        }
        viewModel.uiState.test {
            val uiState = awaitItem()
            assertThat(uiState.uiState).isEqualTo(UiState.Idle)
            assertThat(uiState.suggestions).isNotEmpty()
        }
    }

    @Test
    fun `when updating search query, suggestions are updated`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().suggestions).isNotEmpty()
            viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("1")))
            assertThat(awaitItem().suggestions).isEmpty()
            viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("")))
            skipItems(2)    // skip textField and button state update
            assertThat(awaitItem().suggestions).isNotEmpty()
        }
    }

    @Test
    fun `when search triggered, hide keyboard and scroll list to top`() = runTest {
        viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("1")))
        viewModel.event.test {
            skipItems(1)    // initial ShowKeyboard
            viewModel.onAction(SearchScreenAction.TriggerSearch)
            assertThat(awaitItem()).isEqualTo(SearchScreenEvent.ShowKeyboard(false))
            assertThat(awaitItem()).isEqualTo(SearchScreenEvent.ScrollListToTop)
        }
    }

    @Test
    fun `when editing search query, search button enabled state is updated`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().searchButtonEnabled).isFalse()
            viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("1")))
            assertThat(awaitItem().searchButtonEnabled).isTrue()
            viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("")))
            skipItems(2)
            assertThat(awaitItem().searchButtonEnabled).isFalse()
        }
    }

    @Test
    fun `when clearing search, search field is cleared`() = runTest {
        viewModel.onAction(SearchScreenAction.SearchTextChanged(TextFieldValue("1")))
        viewModel.uiState.test {
            skipItems(1)
            viewModel.onAction(SearchScreenAction.ClearSearch)
            assertThat(awaitItem().searchFieldValue.text).isEmpty()
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `when selecting media, redirect to media screen`() = runTest {
        viewModel.event.test {
            skipItems(1)
            viewModel.onAction(SearchScreenAction.MediaSelected("1"))
            assertThat(awaitItem()).isEqualTo(SearchScreenEvent.SelectMedia("1"))
        }
    }
}