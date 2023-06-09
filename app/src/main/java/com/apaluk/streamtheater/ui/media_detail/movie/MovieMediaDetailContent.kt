package com.apaluk.streamtheater.ui.media_detail.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.apaluk.streamtheater.ui.common.composable.MediaTitle
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media_detail.common.CrewMembers
import com.apaluk.streamtheater.ui.media_detail.MediaDetailAction
import com.apaluk.streamtheater.ui.media_detail.MediaDetailScreenUiState
import com.apaluk.streamtheater.ui.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media_detail.common.MediaDetailPoster
import com.apaluk.streamtheater.ui.media_detail.common.StColors
import com.apaluk.streamtheater.ui.media_detail.util.generalInfoText
import com.apaluk.streamtheater.ui.media_detail.util.isInProgress
import com.apaluk.streamtheater.ui.media_detail.util.relativeProgress
import com.apaluk.streamtheater.R

@Composable
fun MovieMediaDetailContent(
    screenUiState: MediaDetailScreenUiState,
    movieUiState: MovieMediaDetailUiState,
    onMediaDetailAction: (MediaDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaDetailMovie = movieUiState.movie
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        MediaDetailPoster(
            imageUrl = mediaDetailMovie.imageUrl,
            duration = mediaDetailMovie.duration,
            onPlay = { onMediaDetailAction(MediaDetailAction.PlayDefault) },
            progress = mediaDetailMovie.relativeProgress,
            showPlayButton = screenUiState.streamsUiState?.selectedStreamId != null
        )
        Spacer(modifier = Modifier.height(24.dp))
        MediaTitle(
            title = mediaDetailMovie.title,
            originalTitle = mediaDetailMovie.originalTitle
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if(mediaDetailMovie.progress?.isWatched == true) {
                Image(
                    modifier = Modifier.padding(end = 8.dp).size(18.dp),
                    painter = painterResource(id = R.drawable.ic_check_circle_24),
                    contentDescription = "Is watched",
                    colorFilter = ColorFilter.tint(StColors.watchedMedia)
                )
            } else if(mediaDetailMovie.progress?.isInProgress == true) {
                Image(
                    modifier = Modifier.padding(end = 8.dp).size(18.dp),
                    painter = painterResource(id = R.drawable.ic_pause_circle_24),
                    contentDescription = "Is in progress",
                    colorFilter = ColorFilter.tint(StColors.pausedMedia)
                )
            }
            Text(
                text = mediaDetailMovie.generalInfoText(),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        CrewMembers(
            role = stringResourceSafe(id = R.string.st_media_director),
            members = mediaDetailMovie.directors,
            modifier = Modifier.padding(top = 16.dp)
        )
        CrewMembers(
            modifier = Modifier.padding(top = 8.dp),
            role = stringResourceSafe(id = R.string.st_media_writer),
            members = mediaDetailMovie.writer
        )
        CrewMembers(
            modifier = Modifier.padding(top = 8.dp),
            role = stringResourceSafe(id = R.string.st_media_cast),
            members = mediaDetailMovie.cast
        )
        mediaDetailMovie.plot?.let { plot ->
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                text = plot,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}