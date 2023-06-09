package com.apaluk.streamtheater.ui.player

import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
import com.apaluk.streamtheater.core.navigation.StNavActions
import com.apaluk.streamtheater.core.util.millisToSeconds
import com.apaluk.streamtheater.ui.common.composable.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Composable
fun PlayerScreen(
    navActions: StNavActions,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SingleEventHandler(uiState.navigateUpEvent) {
        navActions.navigateUp()
    }
    UiStateAnimator(uiState = uiState.uiState) {
        uiState.videoUrl?.let {
            VideoPlayer(
                uri = Uri.parse(it),
                uiState = uiState,
                onPlayerScreenAction = viewModel::onPlayerScreenAction
            )
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(
    uri: Uri,
    uiState: PlayerScreenState,
    onPlayerScreenAction: (PlayerScreenAction) -> Unit
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
                                newPosition.positionMs.millisToSeconds().toInt(),
                                duration.millisToSeconds().toInt()
                            )
                        )
                    }
                })
                prepare()
            }
    }
    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        })
    ) {
        onDispose { exoPlayer.release() }
    }
    OnLifecycleEvent { _, event ->
        when(event) {
            Lifecycle.Event.ON_PAUSE -> {
                playerState.wasPlayingBeforeOnPause = exoPlayer.isPlaying
                exoPlayer.pause()
            }
            Lifecycle.Event.ON_RESUME -> {
                if(playerState.wasPlayingBeforeOnPause)
                    exoPlayer.play()
            }
            else -> {}
        }
    }
    SingleEventHandler(uiState.startFromPositionEvent) {
        exoPlayer.seekTo(TimeUnit.SECONDS.toMillis(it.toLong()))
        exoPlayer.playWhenReady = true
    }
    LaunchedEffect(Unit) {
        playerState.isPlaying.collectLatest { isPlaying ->
            if(isPlaying) {
                val duration = exoPlayer.duration.millisToSeconds().toInt()
                exoPlayer.startUpdatingVideoProgress {
                    onPlayerScreenAction(
                        PlayerScreenAction.VideoProgressChanged(it, duration)
                    )
                }
            }
            else {
                onPlayerScreenAction(
                    PlayerScreenAction.VideoProgressChanged(
                        exoPlayer.currentPosition.millisToSeconds().toInt(),
                        exoPlayer.duration.millisToSeconds().toInt()
                    )
                )
            }
        }
    }

}

private suspend fun ExoPlayer.startUpdatingVideoProgress(
    onProgressSeconds: (Int) -> Unit
) {
    while(true) {
        delay(5_000)
        onProgressSeconds(currentPosition.millisToSeconds().toInt())
    }
}