@file:OptIn(ExperimentalMaterial3Api::class)

package com.apaluk.streamtheater.ui.media.media_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.domain.model.media.*
import com.apaluk.streamtheater.ui.common.composable.BackButton
import com.apaluk.streamtheater.ui.common.composable.EventHandler
import com.apaluk.streamtheater.ui.common.composable.ProgressBarDialog
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.media.MediaEvent
import com.apaluk.streamtheater.ui.media.MediaViewModel
import com.apaluk.streamtheater.ui.media.media_detail.movie.MovieMediaDetailContent
import com.apaluk.streamtheater.ui.media.media_detail.streams.MediaDetailStreams
import com.apaluk.streamtheater.ui.media.media_detail.tv_show.TvShowMediaDetailContent
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun MediaDetailScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onPlayStream: () -> Unit = {},
    viewModel: MediaDetailViewModel = hiltViewModel(),
    mediaViewModel: MediaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    TopAppBar(
        navigationIcon = {
            BackButton(onBack = { onNavigateUp() })
        },
        title = {}
    )
    UiStateAnimator(uiState = uiState.uiState) {
        MediaDetailScreenContent(
            modifier = modifier,
            uiState = uiState,
            onMediaDetailAction = viewModel::onAction
        )
    }
    EventHandler(viewModel.event) { event ->
        when (event) {
            is MediaDetailEvent.PlayStream -> {
                mediaViewModel.playStreamParams.value = event.params
                onPlayStream()
            }
        }
    }
    EventHandler(mediaViewModel.event) { event ->
        when (event) {
            is MediaEvent.SkipToNextVideo -> viewModel.onAction(MediaDetailAction.SkipToNextVideo)
            is MediaEvent.SkipToPreviousVideo -> viewModel.onAction(MediaDetailAction.SkipToPreviousVideo)
        }
    }
}

@Composable
fun MediaDetailScreenContent(
    uiState: MediaDetailScreenUiState,
    modifier: Modifier = Modifier,
    onMediaDetailAction: (MediaDetailAction) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        uiState.mediaDetailUiState?.let {
            MediaDetailContent(
                screenUiState = uiState,
                mediaDetailUiState = it,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 76.dp, end = 24.dp)
                    .align(Alignment.Top),
                onMediaDetailAction = onMediaDetailAction
            )
        }
        Box(
            modifier = modifier
                .width(340.dp)
                .padding(8.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            uiState.streamsUiState?.let { streamsUiState ->
                if(streamsUiState.streams.isNotEmpty()) {
                    MediaDetailStreams(
                        streamsUiState = streamsUiState,
                        modifier = modifier.fillMaxWidth(),
                        onStreamSelected = { stream ->
                            onMediaDetailAction(MediaDetailAction.PlayStream(stream))
                        }
                    )
                }
            }
        }
    }
    if(uiState.showSeekingProgressBar) {
        ProgressBarDialog()
    }
}

@Composable
fun MediaDetailContent(
    screenUiState: MediaDetailScreenUiState,
    mediaDetailUiState: MediaDetailUiState,
    modifier: Modifier = Modifier,
    onMediaDetailAction: (MediaDetailAction) -> Unit = {}
) {
    when(mediaDetailUiState) {
        is MovieMediaDetailUiState -> MovieMediaDetailContent(
            screenUiState = screenUiState,
            movieUiState = mediaDetailUiState,
            onMediaDetailAction = onMediaDetailAction,
            modifier = modifier
        )
        is TvShowMediaDetailUiState -> TvShowMediaDetailContent(
            tvShowUiState = mediaDetailUiState,
            onMediaDetailAction = onMediaDetailAction,
            modifier = modifier
        )
    }
}

@Preview(device = "spec:width=1600dp,height=800dp,orientation=landscape")
@Composable
fun MediaDetailContentPreview() {
    StTheme {
        MediaDetailScreenContent(
            uiState = MediaDetailScreenUiState(
                uiState = UiState.Content,
                mediaDetailUiState = MovieMediaDetailUiState(
                    MediaDetailMovie(
                        id = "",
                        title = "Pulp fiction",
                        originalTitle = "Pulp fiction",
                        year = "1994",
                        directors = listOf("Quentin Tarantino"),
                        writer = listOf("Quentin Tarantino"),
                        cast = listOf("Bruce Willis", "John Travolta", "Samuel L. Jackson"),
                        genre = listOf("Thriller", "Comedy"),
                        plot = LoremIpsum(50).values.joinToString(" "),
                        imageUrl = null,
                        duration = 7444,
                    )
                ),
                streamsUiState = StreamsUiState(DUMMY_MEDIA_STREAMS)
            ),
            onMediaDetailAction = {}
        )
    }
}
