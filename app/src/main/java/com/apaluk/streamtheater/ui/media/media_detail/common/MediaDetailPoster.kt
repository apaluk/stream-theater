package com.apaluk.streamtheater.ui.media.media_detail.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.formatDuration
import com.apaluk.streamtheater.ui.common.composable.TextWithContrastBackground
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun MediaDetailPoster(
    imageUrl: String?,
    duration: Int? = null,
    bottomTexts: List<String?> = emptyList(),
    mainButtonAction: MediaDetailPosterMainAction = MediaDetailPosterMainAction.None,
    showPreviousButton: Boolean = false,
    showNextButton: Boolean = false,
    onMainButtonClicked: () -> Unit = {},
    onPreviousClicked: () -> Unit = {},
    onNextClicked: () -> Unit = {},
    progress: Float? = null,
) {
    val prevNextButtonsDistance = 148
    Column {
        Box(
            modifier = Modifier
                .aspectRatio(if (imageUrl == null) 3f else 2f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            if (imageUrl != null) {
                Crossfade(
                    targetState = imageUrl,
                    animationSpec = tween(Constants.VERY_LONG_ANIM_DURATION),
                    label = "Crossfade in MediaDetailPoster"
                ) { url ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            if (mainButtonAction != MediaDetailPosterMainAction.None) {
                MediaDetailPosterButton(
                    painter = painterResource(requireNotNull(mainButtonAction.iconRes)),
                    contentDescription = stringResourceSafe(requireNotNull(mainButtonAction.contentDescriptionRes)),
                    modifier = Modifier.size(76.dp).align(Alignment.Center),
                    onClick = onMainButtonClicked
                )
            }
            if (showPreviousButton) {
                MediaDetailPosterButton(
                    painter = painterResource(R.drawable.ic_skip_previous_24),
                    contentDescription = stringResourceSafe(R.string.st_media_previous),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-prevNextButtonsDistance).dp)
                        .size(56.dp),
                    onClick = onPreviousClicked
                )
            }
            if (showNextButton) {
                MediaDetailPosterButton(
                    painter = painterResource(R.drawable.ic_skip_next_24),
                    contentDescription = stringResourceSafe(R.string.st_media_next),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = prevNextButtonsDistance.dp)
                        .size(56.dp),
                    onClick = onNextClicked
                )
            }
            duration?.let { duration ->
                val formattedDuration = remember(duration) { duration.formatDuration() }
                TextWithContrastBackground(
                    text = formattedDuration,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                bottomTexts
                    .filterNotNull()
                    .filter { it.isNotBlank() }
                    .forEach {
                        TextWithContrastBackground(
                            text = it,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress ?: 0f)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
fun MediaDetailPosterButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable { onClick() }
            .padding(8.dp)
            .alpha(0.9f),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseSurface),
    )
}

sealed class MediaDetailPosterMainAction(
    @DrawableRes val iconRes: Int?,
    @StringRes val contentDescriptionRes: Int?,
) {
    object None: MediaDetailPosterMainAction(null, null)
    object Play: MediaDetailPosterMainAction(R.drawable.ic_play_64, R.string.st_media_play)
    object Restart: MediaDetailPosterMainAction(R.drawable.ic_replay_24, R.string.st_media_restart)

    companion object {
        fun from(shouldShowButton: Boolean, isMediaWatched: Boolean): MediaDetailPosterMainAction {
            return when {
                shouldShowButton.not() -> None
                isMediaWatched -> Restart
                else -> Play
            }
        }
    }
}

@Preview(widthDp = 640, heightDp = 360)
@Preview(widthDp = 900, heightDp = 500)
@Preview(widthDp = 400, heightDp = 280)
@Composable
fun MediaDetailPosterPreview() {
    StTheme {
        MediaDetailPoster(
            imageUrl = "https://image.tmdb.org/t/p/w500/6KErczPBROQty7QoIsaa6wJYXZi.jpg",
            duration = 123456,
            bottomTexts = listOf("S03E04", "Ozymandias"),
            mainButtonAction = MediaDetailPosterMainAction.Play,
            showPreviousButton = true,
            showNextButton = true,
            onMainButtonClicked = {},
            progress = 0.5f,
        )
    }

}
