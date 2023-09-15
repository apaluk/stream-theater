@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.apaluk.streamtheater.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.SingleEvent
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.ui.common.composable.BackButton
import com.apaluk.streamtheater.ui.common.composable.DefaultEmptyState
import com.apaluk.streamtheater.ui.common.composable.EventHandler
import com.apaluk.streamtheater.ui.common.composable.StButton
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.util.PreviewDevices
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateToMediaDetail: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // is there a better way to send events to child composables?
    val scrollListToTopFlow = remember { SingleEvent<SearchScreenEvent.ScrollListToTop>() }
    val showKeyboardFlow = remember { SingleEvent<SearchScreenEvent.ShowKeyboard>() }

    EventHandler(viewModel.event) { event ->
        when (event) {
            is SearchScreenEvent.SelectMedia -> onNavigateToMediaDetail(event.mediaId)
            is SearchScreenEvent.ScrollListToTop -> scrollListToTopFlow.emit(event)
            is SearchScreenEvent.ShowKeyboard -> showKeyboardFlow.emit(event)
        }
    }
    SearchScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSearchScreenAction = viewModel::onAction,
        onBack = { onNavigateUp() },
        showKeyboardEvent = showKeyboardFlow.flow,
        scrollListToTopEvent = scrollListToTopFlow.flow
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    onSearchScreenAction: (SearchScreenAction) -> Unit,
    showKeyboardEvent: Flow<SearchScreenEvent.ShowKeyboard>,
    scrollListToTopEvent: Flow<SearchScreenEvent.ScrollListToTop>,
    onBack: () -> Unit
) {
    val toolbarHeight = 82.dp
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.height(toolbarHeight),
                navigationIcon = {
                    BackButton(
                        modifier = Modifier.height(toolbarHeight),
                        onBack = { onBack() }
                    )
                },
                title = {
                    SearchBar(
                        modifier = Modifier.height(toolbarHeight),
                        uiState = uiState,
                        onSearchScreenAction = onSearchScreenAction,
                        showKeyboardEvent = showKeyboardEvent
                    )
                }
            )
        },
        content = { paddingValues ->
            UiStateAnimator(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                uiState = uiState.uiState,
                empty = { DefaultEmptyState(text = stringResourceSafe(id = R.string.st_search_empty_results))},
                idle = {
                    uiState.searchSuggestions?.let {
                        SearchHistoryList(
                            searchHistoryList = it,
                            onSearchScreenAction = onSearchScreenAction,
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                        )
                    }
                }
            ) {
                SearchResults(
                    modifier = Modifier.padding(paddingValues),
                    results = uiState.searchResults,
                    onResultClicked = { onSearchScreenAction(SearchScreenAction.MediaSelected(it)) },
                    scrollToTopEvent = scrollListToTopEvent
                )
            }
        }
    )
}

@Composable
fun SearchBar(
    uiState: SearchUiState,
    onSearchScreenAction: (SearchScreenAction) -> Unit,
    showKeyboardEvent: Flow<SearchScreenEvent.ShowKeyboard>,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val textFieldValue by remember(uiState.searchInput) {
        mutableStateOf(uiState.searchInput.toTextFieldValue())
    }
    EventHandler(showKeyboardEvent) {
        if(it.show) focusRequester.requestFocus() else keyboardController?.hide()
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .fillMaxHeight()
                .focusRequester(focusRequester),
            value = textFieldValue,
            textStyle = MaterialTheme.typography.titleLarge,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onValueChange = {
                onSearchScreenAction(SearchScreenAction.SearchTextChanged(it.text, it.selection.start))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(uiState.searchInput.text.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                keyboardController?.show()
                                focusRequester.requestFocus()
                                onSearchScreenAction(SearchScreenAction.ClearSearch)
                            },
                        painter = painterResource(id = R.drawable.ic_clear_24),
                        contentDescription = null
                    )
                }
            },
            placeholder = {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResourceSafe(id = R.string.st_search_hint),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchScreenAction(SearchScreenAction.TriggerSearch)
                }
            ),
            maxLines = 1
        )
        StButton(
            text = stringResourceSafe(id = R.string.st_search_button),
            onClick = {
                onSearchScreenAction(SearchScreenAction.TriggerSearch)
            },
            enabled = uiState.searchInput.text.isNotBlank(),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

fun TextFieldInput.toTextFieldValue(): TextFieldValue =
    TextFieldValue(
        text = text,
        selection = TextRange(cursorPosition)
    )

@PreviewDevices
@Composable
fun SearchScreenSuggestionsPreview() {
    StTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                searchInput = TextFieldInput("pulp fiction", 0),
                searchSuggestions = listOf("pulp fiction", "pulp fiction 2"),
            ),
            onSearchScreenAction = {},
            onBack = {},
            showKeyboardEvent = emptyFlow(),
            scrollListToTopEvent = emptyFlow()
        )
    }
}

@PreviewDevices
@Composable
fun SearchScreenResultsPreview() {
    StTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                uiState = UiState.Content,
                searchResults = listOf(
                    SearchResultItem(
                        id = "1",
                        title = "The Shawshank Redemption",
                        originalTitle = "The Shawshank Redemption",
                        generalInfo = "Drama, Crime | 1994 | 142 mins",
                        cast = "Tim Robbins, Morgan Freeman",
                        imageUrl = "https://example.com/movie1.jpg"
                    ),
                    SearchResultItem(
                        id = "2",
                        title = "The Godfather",
                        originalTitle = "The Godfather",
                        generalInfo = "Crime, Drama | 1972 | 175 mins",
                        cast = "Marlon Brando, Al Pacino",
                        imageUrl = "https://example.com/movie2.jpg"
                    ),
                    SearchResultItem(
                        id = "3",
                        title = "Pulp Fiction",
                        originalTitle = "Pulp Fiction",
                        generalInfo = "Crime, Drama | 1994 | 154 mins",
                        cast = "John Travolta, Samuel L. Jackson",
                        imageUrl = "https://example.com/movie3.jpg"
                    ),
                    // Add more SearchResultItem objects as needed
                )
            ),
            onSearchScreenAction = {},
            showKeyboardEvent = emptyFlow(),
            scrollListToTopEvent = emptyFlow(),
            onBack = {}
        )
    }
}