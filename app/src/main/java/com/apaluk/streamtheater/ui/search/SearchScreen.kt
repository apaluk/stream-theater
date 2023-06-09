@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.apaluk.streamtheater.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.navigation.StNavActions
import com.apaluk.streamtheater.ui.common.composable.BackButton
import com.apaluk.streamtheater.ui.common.composable.DefaultEmptyState
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.composable.StButton
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchScreen(
    navActions: StNavActions,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    SearchScreenContent(
        modifier = modifier,
        uiState = uiState.value,
        onSearchScreenAction = viewModel::onSearchScreenAction,
        onBack = { navActions.navigateUp() }
    )
    LaunchedEffect(uiState.value.selectedMediaId) {
        uiState.value.selectedMediaId?.let {
            navActions.navigateToMediaDetail(it)
        }
        viewModel.onSearchScreenAction(SearchScreenAction.MediaSelected(null))
    }
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    onSearchScreenAction: (SearchScreenAction) -> Unit,
    onBack: () -> Unit
) {
    val toolbarHeight by remember { mutableStateOf(82.dp) }
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
                        onSearchScreenAction = onSearchScreenAction
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
                    scrollToTop = uiState.scrollListToTop
                )
            }
        }
    )
}

@Composable
fun SearchBar(
    uiState: SearchUiState,
    onSearchScreenAction: (SearchScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = uiState.searchText))
    }
    LaunchedEffect(Unit) {
        uiState.moveSearchFieldCursorToTheEndEvent.flow.collectLatest {
            textFieldValue = TextFieldValue(
                selection = TextRange(it.length),
                text = it
            )
        }
    }
    LaunchedEffect(Unit) {
        uiState.showKeyboardEvent.flow.collectLatest { show ->
            if(show)
                focusRequester.requestFocus()
            else
                keyboardController?.hide()
        }
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
                onSearchScreenAction(SearchScreenAction.SearchTextChanged(it.text))
                textFieldValue = it
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(uiState.searchText.isNotEmpty()) {
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
            enabled = uiState.searchText.isNotBlank(),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    StTheme {
        SearchScreenContent(
            uiState = SearchUiState(),
            onSearchScreenAction = {},
            onBack = {}
        )
    }
}

