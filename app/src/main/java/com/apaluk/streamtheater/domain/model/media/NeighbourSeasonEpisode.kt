package com.apaluk.streamtheater.domain.model.media


data class FindNeighbourSeasonEpisodeResult(
    val seasonIndex: Int?,
    val seasonId: String?,
    val episodeIndex: Int,
    val episodeId: String,
    val seasonHasChanged: Boolean
)

enum class SeasonEpisodeNeighbourType {
    Previous, Next
}