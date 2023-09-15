package com.apaluk.streamtheater.ui.media.media_detail.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.commaSeparatedList
import com.apaluk.streamtheater.core.util.withLeadingZeros
import com.apaluk.streamtheater.domain.model.media.MediaDetail
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.MediaProgress
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailScreenUiState
import com.apaluk.streamtheater.ui.media.media_detail.MediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.MovieMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.StreamsUiState
import com.apaluk.streamtheater.ui.media.media_detail.TvShowMediaDetailUiState
import com.apaluk.streamtheater.ui.media.media_detail.tv_show.TvShowPosterData

private const val MEDIA_INFO_SEPARATOR = "  ${Constants.CHAR_BULLET}  "

fun MediaDetail.toMediaDetailUiState(): MediaDetailUiState =
    when(this) {
        is MediaDetailMovie -> MovieMediaDetailUiState(movie = this)
        is MediaDetailTvShow -> TvShowMediaDetailUiState(
            tvShow = this,
            posterData = TvShowPosterData(imageUrl = imageUrl)
        )
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
    if(episode == null)
        null
    else if(season != null)
        "S${season.orderNumber.withLeadingZeros(2)}E${episode.orderNumber.withLeadingZeros(2)}"
    else
        "E${episode.orderNumber.withLeadingZeros(2)}"

@Composable
@ReadOnlyComposable
fun TvShowSeason.requireName(): String =
    title ?: stringResourceSafe(id = R.string.st_tv_show_season_number, orderNumber)

@Composable
@ReadOnlyComposable
fun TvShowMediaDetailUiState.selectedSeasonName(): String? = selectedSeason()?.requireName()

@Composable
@ReadOnlyComposable
fun TvShowMediaDetailUiState.generalInfoText(): String {
    val info = mutableListOf<String>()
    val seasonYears = seasons?.map { it.year }?.sortedBy { it?.toInt() ?: 0 }
    if (seasonYears.isNullOrEmpty().not()) {
        if(seasonYears?.size == 1) {
            seasonYears.first()?.let { info.add(it) }
        }
        else {
            info.add("${seasonYears?.first()} ${Constants.CHAR_DASH} ${seasonYears?.last()}")
        }
    }
    info.add(stringResourceSafe(id = R.string.st_tv_show_count_of_seasons, tvShow.numSeasons))
    tvShow.genre.commaSeparatedList(3)?.let { info.add(it) }
    return info.joinToString(separator = MEDIA_INFO_SEPARATOR)
}


