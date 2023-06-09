package com.apaluk.streamtheater.ui.media_detail.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.formatDuration
import com.apaluk.streamtheater.ui.common.composable.TextWithContrastBackground
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.R

@Composable
fun MediaDetailPoster(
    imageUrl: String?,
    duration: Int? = null,
    bottomStartTexts: List<String?> = emptyList(),
    onPlay: () -> Unit,
    progress: Float? = null,
    showPlayButton: Boolean = false
) {
    Column {
        Box(
            modifier = Modifier
                .aspectRatio(if (imageUrl == null) 3f else 2f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            imageUrl?.let { url ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(durationMillis = Constants.SHORT_ANIM_DURATION)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if(showPlayButton) {
                Image(
                    painter = painterResource(id = R.drawable.ic_play_filled_circle_64),
                    contentDescription = stringResourceSafe(id = R.string.st_media_play),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .clickable { onPlay() }
                        .align(Alignment.Center)
                        .alpha(0.7f),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inverseSurface)
                )
            }
            duration?.let { duration ->
                TextWithContrastBackground(
                    text = duration.formatDuration(),
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
                bottomStartTexts
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
fun CrewMembers(
    role: String,
    members: List<String>,
    modifier: Modifier = Modifier
) {
    Row {
        Text(
            modifier = modifier
                .alignByBaseline()
                .padding(end = 8.dp),
            text = "$role:",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = modifier.alignByBaseline(),
            text = members
                .take(12)
                .joinToString(", "),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Composable
fun DropDownSelector(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(color = MaterialTheme.colorScheme.inverseSurface)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}