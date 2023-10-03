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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.ui.common.composable.BackButton
import com.apaluk.streamtheater.ui.common.composable.DefaultEmptyState
import com.apaluk.streamtheater.ui.common.composable.EventHandler
import com.apaluk.streamtheater.ui.common.composable.StButton
import com.apaluk.streamtheater.ui.common.composable.TextFieldKeyboardState
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.composable.rememberTextFieldKeyboardState
import com.apaluk.streamtheater.ui.common.util.PreviewDevices
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateToMediaDetail: (String) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val searchFieldKeyboardState = rememberTextFieldKeyboardState()
    val searchResultsListState = rememberLazyListState()

    EventHandler(viewModel.event) { event ->
        when (event) {
            is SearchScreenEvent.SelectMedia -> onNavigateToMediaDetail(event.mediaId)
            is SearchScreenEvent.ScrollListToTop -> searchResultsListState.scrollToItem(0)
            is SearchScreenEvent.ShowKeyboard -> searchFieldKeyboardState.showKeyboard(event.show)
        }
    }
    SearchScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSearchScreenAction = viewModel::onAction,
        onNavigateBack = { onNavigateUp() },
        searchFieldKeyboardState = searchFieldKeyboardState,
        searchResultListState = searchResultsListState
    )
}

@Composable
private fun SearchScreenContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onSearchScreenAction: (SearchScreenAction) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    searchFieldKeyboardState: TextFieldKeyboardState = rememberTextFieldKeyboardState(),
    searchResultListState: LazyListState = rememberLazyListState()
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
                        onClick = { onNavigateBack() }
                    )
                },
                title = {
                    SearchBar(
                        textFieldValue = uiState.searchFieldValue,
                        searchButtonEnabled = uiState.searchButtonEnabled,
                        modifier = Modifier.height(toolbarHeight),
                        onSearchScreenAction = onSearchScreenAction,
                        searchFieldKeyboardState = searchFieldKeyboardState
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
                    if (uiState.suggestions.isNotEmpty()) {
                        SearchHistoryList(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize(),
                            searchHistoryList = uiState.suggestions,
                            onItemSelected = { text -> onSearchScreenAction(
                                SearchScreenAction.SearchTextChanged(TextFieldValue(text, TextRange(text.length)))
                            ) },
                            onTriggerSearch = { onSearchScreenAction(SearchScreenAction.TriggerSearch) },
                            onDeleteItem = { onSearchScreenAction(SearchScreenAction.DeleteSearchHistoryEntry(it)) }
                        )
                    }
                }
            ) {
                SearchResults(
                    modifier = Modifier.padding(paddingValues),
                    results = uiState.results,
                    onResultClicked = { onSearchScreenAction(SearchScreenAction.MediaSelected(it)) },
                    listState = searchResultListState
                )
            }
        }
    )
}

@Composable
fun SearchBar(
    textFieldValue: TextFieldValue,
    searchButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    onSearchScreenAction: (SearchScreenAction) -> Unit = {},
    searchFieldKeyboardState: TextFieldKeyboardState = rememberTextFieldKeyboardState(),
) {
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
                .focusRequester(searchFieldKeyboardState.focusRequester),
            value = textFieldValue,
            textStyle = MaterialTheme.typography.titleLarge,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onValueChange = {
                onSearchScreenAction(SearchScreenAction.SearchTextChanged(it))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(textFieldValue.text.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                searchFieldKeyboardState.showKeyboard(true)
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
            enabled = searchButtonEnabled,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}


@PreviewDevices
@Composable
fun SearchScreenSuggestionsPreview() {
    StTheme {
        SearchScreenContent(
            uiState = SearchUiState(
                searchFieldValue = TextFieldValue("pulp fiction"),
                suggestions = listOf("pulp fiction", "pulp fiction 2"),
            ),
            onSearchScreenAction = {},
            onNavigateBack = {},
            searchFieldKeyboardState = rememberTextFieldKeyboardState(),
            searchResultListState = rememberLazyListState()
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
                results = listOf(
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
                )
            ),
            onSearchScreenAction = {},
            searchFieldKeyboardState = rememberTextFieldKeyboardState(),
            searchResultListState = rememberLazyListState(),
            onNavigateBack = {}
        )
    }
}