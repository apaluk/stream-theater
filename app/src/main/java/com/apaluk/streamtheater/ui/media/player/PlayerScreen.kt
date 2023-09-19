package com.apaluk.streamtheater.ui.media.player

import android.net.Uri
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.ControllerVisibilityListener
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.millisToSeconds
import com.apaluk.streamtheater.ui.common.composable.*
import com.apaluk.streamtheater.ui.media.MediaAction
import com.apaluk.streamtheater.ui.media.MediaViewModel
import com.apaluk.streamtheater.ui.media.media_detail.util.PlayerMediaInfo
import com.apaluk.streamtheater.ui.theme.videoPlayerControl
import kotlinx.coroutines.flow.collectLatest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Composable
fun PlayerScreen(
    onNavigateUp: () -> Unit = {},
    viewModel: PlayerViewModel = hiltViewModel(),
    mediaViewModel: MediaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    EventHandler(viewModel.event) { event ->
        when (event) {
            is PlayerScreenEvent.NavigateUp -> onNavigateUp()
        }
    }
    UiStateAnimator(uiState = uiState.uiState) {
        uiState.videoUrl?.let { videoUrl ->
            VideoPlayer(
                uri = Uri.parse(videoUrl),
                uiState = uiState,
                onPlayerScreenAction = viewModel::onAction,
                seekToPosition = uiState.seekToPosition,
                onSkipToPreviousVideo = {
                    mediaViewModel.onAction(MediaAction.SkipToPreviousVideo)
                    onNavigateUp()
                },
                onSkipToNextVideo = {
                    mediaViewModel.onAction(MediaAction.SkipToNextVideo)
                    onNavigateUp()
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        // pass params from MediaViewModel to PlayerViewModel
        mediaViewModel.playStreamParams.collectLatest { playStreamParams ->
            viewModel.setParams(playStreamParams)
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    uri: Uri,
    uiState: PlayerScreenUiState,
    onPlayerScreenAction: (PlayerScreenAction) -> Unit,
    seekToPosition: Long? = null,
    onSkipToPreviousVideo: () -> Unit = {},
    onSkipToNextVideo: () -> Unit = {}
) {
    val context = LocalContext.current
    val playerState = remember { PlayerState() }

    KeepScreenOn()
    FullScreen()

    val exoPlayer = remember {
        val defaultDataSourceFactory = OkHttpDataSource.Factory(
            OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build()
        ).setUserAgent(Util.getUserAgent(context, "scc"))
        val mediaSourceFactory = ProgressiveMediaSource.Factory(defaultDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(uri))
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_OFF
                addListener(object: Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if(playbackState == Player.STATE_ENDED) {
                            onPlayerScreenAction(PlayerScreenAction.VideoEnded)
                        }
                    }
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        playerState.isPlaying.value = true
                    }
                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        onPlayerScreenAction(
                            PlayerScreenAction.VideoProgressChanged(
                                VideoProgress(
                                    newPosition.positionMs.millisToSeconds().toInt(),
                                    duration.millisToSeconds().toInt()
                                )
                            )
                        )
                    }
                })
                prepare()
            }
    }
    DisposableEffect(
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
        ) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowSubtitleButton(true)
                    setControllerVisibilityListener(ControllerVisibilityListener { visibility ->
                        val isVisible = visibility == View.VISIBLE && isControllerFullyVisible
                        playerState.isPlayerControlsFullyVisible = isVisible
                        onPlayerScreenAction(PlayerScreenAction.PlayerControlsVisibilityChanged(isVisible))
                    })
                }
            })
        }
    ) {
        onDispose { exoPlayer.release() }
    }

    if(playerState.isPlayerControlsFullyVisible) {
        VideoPlayerOverlay(
            uiState = uiState,
            onSkipToPrevious = onSkipToPreviousVideo,
            onSkipToNext = onSkipToNextVideo,
        )
    }
    OnLifecycleEvent { _, event ->
        when(event) {
            Lifecycle.Event.ON_PAUSE -> {
                playerState.wasPlayingBeforeOnPause = exoPlayer.isPlaying
                exoPlayer.pause()
                onPlayerScreenAction(
                    PlayerScreenAction.VideoProgressChanged(
                        VideoProgress(
                            exoPlayer.currentPosition.millisToSeconds().toInt(),
                            exoPlayer.duration.millisToSeconds().toInt()
                        )
                    )
                )
            }
            Lifecycle.Event.ON_RESUME -> {
                if(playerState.wasPlayingBeforeOnPause)
                    exoPlayer.play()
            }
            else -> {}
        }
    }
    if (seekToPosition != null) {
        LaunchedEffect(seekToPosition) {
            exoPlayer.seekTo(TimeUnit.SECONDS.toMillis(seekToPosition))
            exoPlayer.playWhenReady = true
        }
    }

    // if player stops, update progress
    LaunchedEffect(Unit) {
        playerState.isPlaying.collectLatest { isPlaying ->
            if(!isPlaying)  {
                onPlayerScreenAction(
                    PlayerScreenAction.VideoProgressChanged(
                        VideoProgress(
                            exoPlayer.currentPosition.millisToSeconds().toInt(),
                            exoPlayer.duration.millisToSeconds().toInt()
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun VideoPlayerOverlay(
    uiState: PlayerScreenUiState,
    onSkipToPrevious: () -> Unit = {},
    onSkipToNext: () -> Unit = {},
) {
    val bottomPadding = 112.dp
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.showNextPrevButtons) {
                // previous button
                IconButton(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, bottom = bottomPadding)
                        .align(Alignment.CenterVertically),
                    onClick = { onSkipToPrevious() }
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.ic_skip_previous_24),
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.videoPlayerControl
                    )
                }
            }
            // textual media info
            VideoPlayerOverlayMediaInfo(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = bottomPadding),
                mediaInfo = uiState.playerMediaInfo
            )
            if (uiState.showNextPrevButtons) {
                // next button
                IconButton(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp, bottom = bottomPadding)
                        .align(Alignment.CenterVertically),
                    onClick = { onSkipToNext() }
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.ic_skip_next_24),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.videoPlayerControl
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayerOverlayMediaInfo(
    modifier: Modifier = Modifier,
    mediaInfo: PlayerMediaInfo?
) {
    when(mediaInfo) {
        is PlayerMediaInfo.Movie -> VideoPlayerOverlayMovieMediaInfo(
            modifier = modifier,
            movieInfo = mediaInfo
        )
        is PlayerMediaInfo.TvShow -> VideoPlayerOverlayTvShowMediaInfo(
            modifier = modifier,
            tvShowInfo = mediaInfo
        )
        else -> Spacer(modifier = modifier)
    }
}

@Composable
fun VideoPlayerOverlayMovieMediaInfo(
    modifier: Modifier = Modifier,
    movieInfo: PlayerMediaInfo.Movie
) {
    Text(
        modifier = modifier,
        text = movieInfo.title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.videoPlayerControl
    )
}

@Composable
fun VideoPlayerOverlayTvShowMediaInfo(
    modifier: Modifier = Modifier,
    tvShowInfo: PlayerMediaInfo.TvShow
) {
    val episodeText = "${tvShowInfo.seasonEpisode}  ${Constants.CHAR_BULLET}  ${tvShowInfo.episodeName}"
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = tvShowInfo.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.videoPlayerControl
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = episodeText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.videoPlayerControl
        )
    }
}