package com.apaluk.streamtheater.data.stream_cinema.remote.dto.search


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatingsDto(
    @Json(name = "csfd")
    val csfd: CsfdDto?,
    @Json(name = "imdb")
    val imdb: ImdbDto?,
    @Json(name = "Metacritic")
    val metacritic: MetacriticDto?,
    @Json(name = "Rotten Tomatoes")
    val rottenTomatoes: RottenTomatoesDto?,
    @Json(name = "tmdb")
    val tmdb: TmdbDto?,
    @Json(name = "trakt")
    val trakt: TraktDto?
)