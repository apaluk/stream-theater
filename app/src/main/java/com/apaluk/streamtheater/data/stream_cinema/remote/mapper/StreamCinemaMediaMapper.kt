package com.apaluk.streamtheater.data.stream_cinema.remote.mapper

import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.MediaDetailDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.MediaTypeDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.TvShowChildType
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.I18nInfoLabelDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.MediaChildrenResponseDto
import com.apaluk.streamtheater.domain.model.media.MediaDetail
import com.apaluk.streamtheater.domain.model.media.MediaDetailMovie
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.TvShowChild
import com.apaluk.streamtheater.domain.model.media.TvShowEpisode
import com.apaluk.streamtheater.domain.model.media.TvShowSeason
import com.apaluk.streamtheater.domain.model.media.util.orderNumber
import javax.inject.Inject
import kotlin.math.roundToInt

class StreamCinemaMediaMapper @Inject constructor(
    private val resourcesManager: ResourcesManager
) {
    fun toMediaDetail(mediaDetailDto: MediaDetailDto): MediaDetail =
        with(mediaDetailDto) {
            when (infoLabels.mediatype) {
                MediaTypeDto.Movie -> MediaDetailMovie(
                    id = id,
                    title = getTitle(),
                    originalTitle = infoLabels.originaltitle,
                    imageUrl = getImageUrl(),
                    year = infoLabels.year.toString(),
                    directors = infoLabels.director,
                    writer = infoLabels.writer,
                    cast = cast.map { it.name },
                    genre = infoLabels.genre.removeDuplicateGenres(),
                    plot = getPlot(),
                    duration = streamInfo.video?.duration?.roundToInt() ?: infoLabels.duration,
                )

                MediaTypeDto.TvShow -> MediaDetailTvShow(
                    id = id,
                    title = getTitle(),
                    originalTitle = infoLabels.originaltitle,
                    imageUrl = getImageUrl(),
                    cast = cast.map { it.name },
                    genre = infoLabels.genre.removeDuplicateGenres(),
                    plot = getPlot(),
                    numSeasons = childrenCount,
                    duration = streamInfo.video?.duration?.roundToInt() ?: infoLabels.duration,
                )
            }
        }

    fun toListOfTvShowChildren(mediaChildrenResponseDto: MediaChildrenResponseDto): List<TvShowChild> =
        with(mediaChildrenResponseDto) {
            hits.hits.mapIndexed { index, hit ->
                when (hit.source.infoLabels.mediatype) {
                    TvShowChildType.Season -> hit.toTvShowSeason(index)
                    TvShowChildType.Episode -> hit.toTvShowEpisode(index)
                }
            }.sortedBy { it.orderNumber() }
        }


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

    private fun com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.HitDto.toTvShowSeason(index: Int): TvShowSeason =
        TvShowSeason(
            id = id,
            orderNumber = source.infoLabels.season ?: index,
            title = source.infoLabels.originaltitle,
            year = source.infoLabels.year?.toString(),
            directors = source.infoLabels.director,
            writer = source.infoLabels.writer,
            cast = source.cast.map { it.name },
            genre = source.infoLabels.genre.removeDuplicateGenres(),
            plot = source.i18nInfoLabels.getPlot(),
            imageUrl = source.i18nInfoLabels.getFanArtImageUrl(),
        )

    private fun com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.HitDto.toTvShowEpisode(index: Int): TvShowEpisode =
        TvShowEpisode(
            id = id,
            orderNumber = source.infoLabels.episode ?: index,
            title = source.i18nInfoLabels.getTitle() ?: resourcesManager.getString(R.string.st_tv_show_episode_number, index),
            originalTitle = source.infoLabels.originaltitle,
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
}