package com.apaluk.streamtheater.ui.media_detail.tv_show

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.formatDuration
import com.apaluk.streamtheater.core.util.withLeadingZeros
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.MediaProgress
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.ui.common.composable.MediaTitle
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media_detail.MediaDetailAction
import com.apaluk.streamtheater.ui.media_detail.TvShowMediaDetailUiState
import com.apaluk.streamtheater.ui.media_detail.common.DropDownSelector
import com.apaluk.streamtheater.ui.media_detail.common.MediaDetailPoster
import com.apaluk.streamtheater.ui.media_detail.common.StColors
import com.apaluk.streamtheater.ui.media_detail.util.generalInfoText
import com.apaluk.streamtheater.ui.media_detail.util.isInProgress
import com.apaluk.streamtheater.ui.media_detail.util.relativeProgress
import com.apaluk.streamtheater.ui.media_detail.util.requireName
import com.apaluk.streamtheater.ui.media_detail.util.selectedSeasonName
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun TvShowMediaDetailContent(
    tvShowUiState: TvShowMediaDetailUiState,
    modifier: Modifier = Modifier,
    onMediaDetailAction: (MediaDetailAction) -> Unit = {},
) {
    val mediaDetailTvShow = tvShowUiState.tvShow
    val showSeasonSelectorDialog = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        tvShowUiState.posterData?.let { posterData ->
            MediaDetailPoster(
                imageUrl = posterData.imageUrl,
                onPlay = { onMediaDetailAction(MediaDetailAction.PlayDefault) },
                bottomStartTexts = listOf(
                    posterData.episodeNumber,
                    posterData.episodeName
                ),
                duration = posterData.duration,
                progress = posterData.progress,
                showPlayButton = posterData.showPlayButton
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaTitle(
                title = mediaDetailTvShow.title,
                originalTitle = mediaDetailTvShow.originalTitle,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            tvShowUiState.selectedSeasonName()?.let { seasonName ->
                DropDownSelector(
                    text = seasonName,
                    onClick = { showSeasonSelectorDialog.value = true }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mediaDetailTvShow.generalInfoText(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        MediaDetailTvShowEpisodesList(
            tvShowUiState = tvShowUiState,
            onMediaDetailAction = onMediaDetailAction
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
    if (showSeasonSelectorDialog.value) {
        tvShowUiState.seasons?.let { seasons ->
            SelectSeasonDialog(
                seasons = seasons,
                onSeasonIndexSelected = {
                    onMediaDetailAction(MediaDetailAction.SelectTvShowSeason(it))
                    showSeasonSelectorDialog.value = false
                },
                onDismiss = { showSeasonSelectorDialog.value = false }
            )
        }
    }
}

@Composable
private fun SelectSeasonDialog(
    seasons: List<TvShowSeason>,
    onSeasonIndexSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(seasons) { index, season ->
                    Text(
                        text = season.requireName(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeasonIndexSelected(index) }
                            .padding(horizontal = 32.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun MediaDetailTvShowEpisodesList(
    tvShowUiState: TvShowMediaDetailUiState,
    onMediaDetailAction: (MediaDetailAction) -> Unit
) {
    UiStateAnimator(
        uiState = tvShowUiState.episodesUiState,
        modifier = Modifier.heightIn(min = 140.dp)
    ) {
        val episodes = tvShowUiState.episodes ?: return@UiStateAnimator
        Column {
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )
            episodes.forEachIndexed { index, episode ->
                MediaDetailTvShowEpisode(
                    episode = episode,
                    onSelected = { onMediaDetailAction(MediaDetailAction.SelectTvShowEpisode(index)) },
                    isSelected = index == tvShowUiState.selectedEpisodeIndex
                )
                Divider(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun MediaDetailTvShowEpisode(
    episode: TvShowEpisode,
    onSelected: () -> Unit,
    isSelected: Boolean = false
) {
    val background = if(isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .background(background)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.width(120.dp)) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (!episode.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        modifier = Modifier
                            .background(background),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(episode.imageUrl)
                            .crossfade(durationMillis = Constants.SHORT_ANIM_DURATION)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
            episode.relativeProgress?.let { progress ->
                Row {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    )
                }
            }

        }
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 12.dp)
                .width(28.dp)
                .fillMaxHeight(),
            text = episode.orderNumber.withLeadingZeros(2),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = episode.title ?: stringResourceSafe(
                id = R.string.st_tv_show_episode_number,
                episode.orderNumber
            ),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 32.dp)
                .width(60.dp),
            text = episode.duration.formatDuration(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.Center
        ) {
            episode.progress?.let { progress ->
                if (progress.isWatched) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check_circle_24),
                        contentDescription = "Watched",
                        colorFilter = ColorFilter.tint(StColors.watchedMedia),
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterStart)
                    )
                } else if(progress.isInProgress) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pause_circle_24),
                        contentDescription = "In progress",
                        colorFilter = ColorFilter.tint(StColors.pausedMedia),
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TvShowMediaDetailContentPreview() {
    StTheme {
        TvShowMediaDetailContent(
            tvShowUiState = TvShowMediaDetailUiState(
                tvShow = MediaDetailTvShow(
                    id = "",
                    title = "Nezname dejiny Spojenych statu americkych",
                    originalTitle = "The Mandalorian",
                    imageUrl = null,
                    years = null,
                    genre = emptyList(),
                    plot = null,
                    cast = emptyList(),
                    numSeasons = 2,
                    duration = 0,
                    progress = MediaProgress(0, false)
                ),
                episodesUiState = UiState.Content,
                seasons = listOf(
                    TvShowSeason(
                        id = "",
                        orderNumber = 1,
                        title = "Season 1",
                        year = null,
                        directors = emptyList(),
                        writer = emptyList(),
                        cast = emptyList(),
                        genre = emptyList(),
                        plot = null,
                        imageUrl = null,
                    )
                ),
                selectedSeasonIndex = 0
            )
        )
    }

}