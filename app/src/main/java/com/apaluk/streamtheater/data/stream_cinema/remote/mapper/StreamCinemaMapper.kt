package com.apaluk.streamtheater.data.stream_cinema.remote.mapper

import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.requireNotNullOrEmpty
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.MediaDetailDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.MediaTypeDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.TvShowChildType
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.HitDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.I18nInfoLabelDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.SearchResponseDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.MediaStreamsResponseItemDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.SubtitleDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.VideoDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.MediaChildrenResponseDto
import com.apaluk.streamtheater.domain.model.media.*
import com.apaluk.streamtheater.domain.model.media.util.orderNumber
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import kotlin.math.roundToInt

fun SearchResponseDto.toSearchResultItems(): List<SearchResultItem> =
    hits.hits.mapNotNull { it.toSearchResultItem() }

fun HitDto.toSearchResultItem(): SearchResultItem? =
    try {
        SearchResultItem(
            id = id,
            title = getTitle(),
            originalTitle = source.infoLabels.originaltitle,
            year = source.infoLabels.year?.toString().orEmpty(),
            genre = source.infoLabels.genre,
            duration = source.infoLabels.duration ?: 0,
            cast = source.cast.map { it.name },
            director = source.infoLabels.director.orEmpty(),
            imageUrl = getImageUrl()
        )
    } catch (e: Exception) {
        null
    }

fun MediaDetailDto.toMediaDetail(): MediaDetail =
    when(infoLabels.mediatype) {
        MediaTypeDto.Movie ->
            MediaDetailMovie(
            id = id,
            title = getTitle(),
            originalTitle = infoLabels.originaltitle,
            imageUrl = getImageUrl(),
            year = infoLabels.year.toString(),
            directors = infoLabels.director,
            writer = infoLabels.writer,
            cast = cast.map { it.name },
            genre = infoLabels.genre,
            plot = getPlot(),
            duration = streamInfo.video?.duration?.roundToInt() ?: infoLabels.duration,
        )
        MediaTypeDto.TvShow -> MediaDetailTvShow(
            id = id,
            title = getTitle(),
            originalTitle = infoLabels.originaltitle,
            imageUrl = getImageUrl(),
            years = tvShowYears(),
            cast = cast.map { it.name },
            genre = infoLabels.genre,
            plot = getPlot(),
            numSeasons = childrenCount,
            duration = streamInfo.video?.duration?.roundToInt() ?: infoLabels.duration,
        )
    }

fun MediaStreamsResponseItemDto.toMediaStream(): MediaStream {
    val duration = video.firstOrNull()?.duration?.toInt() ?: 0
    return MediaStream(
        ident = ident,
        size = size,
        duration = duration,
        speed = if (duration == 0) 0.0 else size.toDouble() / duration.toDouble(),
        audios = audio
            .map { it.language.orEmpty() }
            .filter { it.isNotBlank() }
            .distinct(),
        video = video.firstOrNull()?.toVideoDefinition() ?: VideoDefinition.SD,
        subtitles = subtitles.mapNotNull { it.toSubtitles() }
    )
}

fun MediaChildrenResponseDto.toListOfTvShowChildren(): List<TvShowChild> =
    hits.hits.mapIndexed { index, hit ->
        when(hit.source.infoLabels.mediatype) {
            TvShowChildType.Season -> hit.toTvShowSeason(index)
            TvShowChildType.Episode -> hit.toTvShowEpisode(index)
        }
    }.sortedBy { it.orderNumber() }


private fun HitDto.getImageUrl(): String? = source.i18nInfoLabels.getPosterImageUrl()

private fun MediaDetailDto.getImageUrl(): String? =
    i18nInfoLabels.getFanArtImageUrl("en")
        ?: i18nInfoLabels.getFanArtImageUrl("sk")
        ?: i18nInfoLabels.getFanArtImageUrl("cs")

private fun List<I18nInfoLabelDto>.getPosterImageUrl(): String? =
    getPosterImageUrl("en") ?: getPosterImageUrl("sk") ?: getPosterImageUrl("cs")

private fun List<I18nInfoLabelDto>.getPosterImageUrl(lang: String): String? =
    this.firstOrNull { it.lang == lang }
        ?.art?.poster

private fun List<I18nInfoLabelDto>.getFanArtImageUrl(): String? =
    getFanArtImageUrl("en") ?: getFanArtImageUrl("sk") ?: getFanArtImageUrl("cs")

private fun List<I18nInfoLabelDto>.getFanArtImageUrl(lang: String): String? =
    this.firstOrNull { it.lang == lang }
        ?.art?.fanart

private fun HitDto.getTitle(): String =
    source.i18nInfoLabels.getTitle("sk")
        ?: source.i18nInfoLabels.getTitle("cs")
        ?: source.i18nInfoLabels.getTitle("en")
        ?: requireNotNull(source.infoLabels.originaltitle)

private fun MediaDetailDto.getPlot(): String = i18nInfoLabels.getPlot()

private fun MediaDetailDto.getTitle(): String =
    i18nInfoLabels.getTitle() ?: infoLabels.originaltitle ?: ""

private fun List<I18nInfoLabelDto>.getTitle(): String? =
    getTitle("sk") ?: getTitle("cs") ?: getTitle("en")

private fun List<I18nInfoLabelDto>.getTitle(lang: String): String? =
    firstOrNull { it.lang == lang }
        ?.title.run {
            // if it's blank, return null
            if(this.isNullOrBlank()) null else this
        }

private fun List<I18nInfoLabelDto>.getPlot(): String =
    getPlot("sk") ?: getPlot("cs") ?: getPlot("en").orEmpty()

private fun List<I18nInfoLabelDto>.getPlot(lang: String): String? =
    firstOrNull { it.lang == lang }
        ?.plot.run {
            // if it's blank, return null
            if(this.isNullOrBlank()) null else this
        }

private fun VideoDto.toVideoDefinition(): VideoDefinition =
    when {
        height > 2500 -> VideoDefinition.UHD_8K
        height > 2000 -> VideoDefinition.UHD_4K
        height > 900 -> VideoDefinition.FHD
        height > 600 -> VideoDefinition.HD
        else -> VideoDefinition.SD
    }

private fun SubtitleDto.toSubtitles(): Subtitles? =
    try {
        Subtitles(
            lang = requireNotNullOrEmpty(language),
            forced = forced ?: false
        )
    } catch (e: Exception) {
        null
    }

private fun com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.HitDto.toTvShowSeason(index: Int): TvShowSeason =
    TvShowSeason(
        id = id,
        orderNumber = source.infoLabels.season ?: index,
        title = source.infoLabels.originaltitle,
        year = source.infoLabels.year?.toString(),
        directors = source.infoLabels.director,
        writer = source.infoLabels.writer,
        cast = source.cast.map { it.name },
        genre = source.infoLabels.genre,
        plot = source.i18nInfoLabels.getPlot(),
        imageUrl = source.i18nInfoLabels.getFanArtImageUrl(),
    )

private fun com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.HitDto.toTvShowEpisode(index: Int): TvShowEpisode =
    TvShowEpisode(
        id = id,
        orderNumber = source.infoLabels.episode ?: index,
        title = source.i18nInfoLabels.getTitle(),
        year = source.infoLabels.year?.toString(),
        directors = source.infoLabels.director,
        writer = source.infoLabels.writer,
        cast = source.cast.map { it.name },
        genre = source.infoLabels.genre,
        plot = source.i18nInfoLabels.getPlot(),
        imageUrl = source.i18nInfoLabels.getFanArtImageUrl(),
        thumbImageUrl = source.i18nInfoLabels.getPosterImageUrl(),
        duration = source.streamInfo?.video?.duration?.roundToInt() ?: source.infoLabels.duration ?: 0
    )

private fun MediaDetailDto.tvShowYears(): String? {
    val startYear = infoLabels.year ?: return null
    return "$startYear ${Constants.CHAR_DASH} ${startYear + childrenCount}"
}
