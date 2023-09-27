package com.apaluk.streamtheater.data.stream_cinema.remote.mapper

import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.core.util.commaSeparatedList
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.HitDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.I18nInfoLabelDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.InfoLabelsDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.SearchResponseDto
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import javax.inject.Inject

class StreamCinemaSearchMapper @Inject constructor() {

    fun toSearchResultItems(searchResponseDto: SearchResponseDto): List<SearchResultItem> =
        searchResponseDto.hits.hits.mapNotNull { it.toSearchResultItem() }

    fun HitDto.toSearchResultItem(): SearchResultItem? =
        try {
            SearchResultItem(
                id = id,
                title = getTitle(),
                originalTitle = source.infoLabels.originaltitle,
                generalInfo = source.infoLabels.generalInfo,
                cast = source.cast.map { it.name }.take(3).joinToString(separator = ", "),
                imageUrl = getImageUrl()
            )
        } catch (e: Exception) {
            null
        }

    private val InfoLabelsDto.generalInfo: String
        get() {
            val info = mutableListOf<String>()
            if (year != null) info.add(year.toString())
            country
                ?.map {it.fixCountryName() }
                ?.distinct()
                ?.commaSeparatedList(3)
                ?.let { info.add(it) }
            genre.commaSeparatedList(3)?.let { info.add(it) }
            return info.joinToString(separator = "  ${Constants.CHAR_BULLET}  ")
        }

    private fun HitDto.getTitle(): String =
        source.i18nInfoLabels.getTitle("sk")
            ?: source.i18nInfoLabels.getTitle("cs")
            ?: source.i18nInfoLabels.getTitle("en")
            ?: requireNotNull(source.infoLabels.originaltitle)


    private fun List<I18nInfoLabelDto>.getTitle(lang: String): String? =
        firstOrNull { it.lang == lang }
            ?.title.run {
                // if it's blank, return null
                if(this.isNullOrBlank()) null else this
            }

    private fun HitDto.getImageUrl(): String? = source.i18nInfoLabels.getPosterImageUrl()

    private fun List<I18nInfoLabelDto>.getPosterImageUrl(): String? =
        getPosterImageUrl("en") ?: getPosterImageUrl("sk") ?: getPosterImageUrl("cs")

    private fun List<I18nInfoLabelDto>.getPosterImageUrl(lang: String): String? =
        this.firstOrNull { it.lang == lang }
            ?.art?.poster

}