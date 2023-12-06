package com.apaluk.streamtheater.domain.use_case.media.util

import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.util.Constants.MEDIA_INFO_SEPARATOR
import com.apaluk.streamtheater.core.util.commaSeparatedList
import com.apaluk.streamtheater.domain.model.media.MediaDetailTvShow
import com.apaluk.streamtheater.domain.model.media.TvShowSeason

fun List<TvShowSeason>.getYears(): String? {
    return if (this.isEmpty())
        null
    else {
        if(size == 1) {
            this.first().year
        } else {
            val sortedYears = this.mapNotNull { it.year }.sorted()
            val first = sortedYears.first()
            val last = sortedYears.last()
            if (first == last) first else "$first - $last"
        }
    }
}

fun MediaDetailTvShow.generalInfoText(
    resourcesManager: ResourcesManager,
    years: String? = null,
): String {
    val info = mutableListOf<String>()
    years?.let { seasonYears -> info.add(seasonYears) }
    info.add(resourcesManager.getString(R.string.st_tv_show_count_of_seasons, numSeasons))
    genre.commaSeparatedList()?.let { info.add(it) }
    return info.joinToString(separator = MEDIA_INFO_SEPARATOR)
}

