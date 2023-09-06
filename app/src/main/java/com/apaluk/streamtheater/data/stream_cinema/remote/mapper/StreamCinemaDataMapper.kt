package com.apaluk.streamtheater.data.stream_cinema.remote.mapper

internal fun String.fixCountryName(): String =
    when (this.lowercase()) {
        "united states of america", "us" -> "USA"
        "jp" -> "Japan"
        "fr" -> "France"
        "cn" -> "China"
        "it" -> "Italy"
        "sk" -> "Slovakia"
        else -> this
    }

internal fun String.fixGenre(): String =
    when (this.lowercase()) {
        "science fiction", "science-fiction" -> "Sci-Fi"
        else -> this
    }

internal fun List<String>.removeDuplicateGenres(): List<String> = map { it.fixGenre() }.distinct()