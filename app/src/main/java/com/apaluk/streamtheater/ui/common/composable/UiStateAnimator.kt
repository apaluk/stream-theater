package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.ComposableFun
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.common.util.text

@Composable
fun UiStateAnimator(
    uiState: UiState,
    modifier: Modifier = Modifier,
    loading: ComposableFun = { FullScreenLoader(modifier) },
    empty: ComposableFun = { DefaultEmptyState(modifier) },
    error: @Composable (String) -> Unit = { text ->
        DefaultErrorState(text = text, modifier = modifier)
    },
    idle: ComposableFun = {},
    content: ComposableFun
) {
    when(uiState) {
        UiState.Idle -> idle()
        UiState.Loading -> loading()
        UiState.Content -> content()
        UiState.Empty -> empty()
        is UiState.Error -> error(uiState.text())
    }
}

@Composable
fun FullScreenLoader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("fullScreenLoader"),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DefaultEmptyState(
    modifier: Modifier = Modifier,
    text: String = stringResourceSafe(id = R.string.st_default_empty_state),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DefaultErrorState(
    modifier: Modifier = Modifier,
    text: String = stringResourceSafe(id = R.string.st_default_error_state),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }

}