package com.apaluk.streamtheater.ui.media.media_detail.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.ui.common.composable.MediaTitle
import com.apaluk.streamtheater.ui.common.util.PreviewDevices
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.common.CrewMembers
import com.apaluk.streamtheater.ui.media.media_detail.common.MediaDetailPoster
import com.apaluk.streamtheater.ui.media.media_detail.common.MediaDetailPosterMainAction
import com.apaluk.streamtheater.ui.media.media_detail.common.StColors
import com.apaluk.streamtheater.ui.media.media_detail.util.generalInfoText
import com.apaluk.streamtheater.ui.media.media_detail.util.isInProgress
import com.apaluk.streamtheater.ui.media.media_detail.util.relativeProgress
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun MovieMediaDetailContent(
    movieUiState: MovieMediaDetailUiState,
    showPlayButton: Boolean,
    modifier: Modifier = Modifier,
    onPlayDefault: () -> Unit = {},
) {
    val mediaDetailMovie = movieUiState.movie
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        MediaDetailPoster(
            imageUrl = mediaDetailMovie.imageUrl,
            duration = mediaDetailMovie.duration,
            onPlay = { onPlayDefault() },
            progress = mediaDetailMovie.relativeProgress,
            mainButtonAction = MediaDetailPosterMainAction.from(showPlayButton, mediaDetailMovie.progress?.isWatched == true)
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
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    painter = painterResource(id = R.drawable.ic_check_circle_24),
                    contentDescription = "Is watched",
                    colorFilter = ColorFilter.tint(StColors.watchedMedia)
                )
            } else if(mediaDetailMovie.progress?.isInProgress == true) {
                Image(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
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

@PreviewDevices
@Composable
fun MovieMediaDetailPreview() {
    StTheme {
        MovieMediaDetailContent(
            movieUiState = MovieMediaDetailUiState(
                movie = MediaDetailMovie(
                    id = "2",
                    title = "The Godfather",
                    originalTitle = "The Godfather",
                    imageUrl = "https://example.com/movie2.jpg",
                    year = "1972",
                    directors = listOf("Francis Ford Coppola"),
                    writer = listOf("Mario Puzo", "Francis Ford Coppola"),
                    cast = listOf("Marlon Brando", "Al Pacino"),
                    genre = listOf("Crime", "Drama"),
                    plot = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                    duration = 175
                )
            ),
            showPlayButton = true,
        )
    }
}