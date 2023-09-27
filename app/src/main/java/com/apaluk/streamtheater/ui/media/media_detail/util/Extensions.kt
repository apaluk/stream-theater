package com.apaluk.streamtheater.ui.media.media_detail.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.util.Constants.MEDIA_INFO_SEPARATOR
import com.apaluk.streamtheater.core.util.commaSeparatedList
import com.apaluk.streamtheater.core.util.withLeadingZeros
import com.apaluk.streamtheater.domain.model.media.MediaDetail
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.MediaProgress
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.use_case.media.util.generalInfoText
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailScreenUiState
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.StreamsUiState
import com.apaluk.streamtheater.ui.media.media_detail.TvShowMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.tv_show.TvShowPosterData


fun MediaDetail.toMediaDetailUiState(resourcesManager: ResourcesManager): MediaDetailUiState =
    when(this) {
        is MediaDetailMovie -> MovieMediaDetailUiState(movie = this)
        is MediaDetailTvShow ->  {
            TvShowMediaDetailUiState(
                tvShow = this.copy(infoText = generalInfoText(resourcesManager)),
                posterData = TvShowPosterData(imageUrl = imageUrl),
                episodesUiState = UiState.Loading   // in this phase we have no episodes yet
            )
        }
    }

fun MediaDetailMovie.generalInfoText(): String {
    with(StringBuilder()) {
        year?.let {
            append(it).append(MEDIA_INFO_SEPARATOR)
        }
        if(genre.isNotEmpty()) {
            append(genre.joinToString(separator = " / "))
        }
        return toString()
    }
}

val MediaDetailMovie.relativeProgress: Float?
    get() = progress?.let { (it.progressSeconds.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }

val MediaProgress.isInProgress: Boolean
    get() = progressSeconds > 0

val MediaDetailScreenUiState.tvShowUiState: TvShowMediaDetailUiState?
    get() = (mediaDetailUiState as? TvShowMediaDetailUiState)

fun TvShowMediaDetailUiState.selectedSeason(): TvShowSeason? =
    if (selectedSeasonIndex != null
        && seasons != null
        && selectedSeasonIndex in seasons.indices)
        seasons[selectedSeasonIndex]
    else null

fun TvShowMediaDetailUiState.selectedEpisode(): TvShowEpisode? =
    if (selectedEpisodeIndex != null
        && episodes != null
        && selectedEpisodeIndex in episodes.indices
    ) episodes[selectedEpisodeIndex]
    else null

val TvShowEpisode.relativeProgress: Float?
    get() = progress?.let { (it.progressSeconds.toFloat() / duration.toFloat()).coerceIn(0f, 1f) }

val StreamsUiState.selectedIndex: Int?
    get() = selectedStreamId?.let { streamId ->
        streams.indexOfFirst { it.ident == streamId }
    }

fun MediaDetailUiState.toPlayerMediaInfo(): PlayerMediaInfo = when(this) {
    is MovieMediaDetailUiState -> PlayerMediaInfo.Movie(title = movie.title)
    is TvShowMediaDetailUiState -> {
        val selectedEpisode = selectedEpisode()
        PlayerMediaInfo.TvShow(
            title = tvShow.title,
            seasonEpisode = seasonEpisodeText(selectedSeason(), selectedEpisode),
            episodeName = selectedEpisode?.title
        )
    }
}

fun seasonEpisodeText(season: TvShowSeason?, episode: TvShowEpisode?): String? =
    seasonEpisodeText(season?.orderNumber, episode?.orderNumber)

fun seasonEpisodeText(seasonOrderNumber: Int?, episodeOrderNumber: Int?): String? =
    if(episodeOrderNumber == null)
        null
    else if(seasonOrderNumber != null)
        "S${seasonOrderNumber.withLeadingZeros(2)}E${episodeOrderNumber.withLeadingZeros(2)}"
    else
        "E${episodeOrderNumber.withLeadingZeros(2)}"


@Composable
@ReadOnlyComposable
fun TvShowSeason.requireName(): String =
    title ?: stringResourceSafe(id = R.string.st_tv_show_season_number, orderNumber)

@Composable
@ReadOnlyComposable
fun TvShowMediaDetailUiState.selectedSeasonName(): String? = selectedSeason()?.requireName()


fun TvShowEpisode.generalInfoText(seasonOrderNumber: Int?): String {
    val info = mutableListOf<String>()
    seasonEpisodeText(seasonOrderNumber, orderNumber)?.let { info.add(it) }
    year?.let { info.add(it) }
    genre.commaSeparatedList(3)?.let { info.add(it) }
    return info.joinToString(separator = MEDIA_INFO_SEPARATOR)
}

