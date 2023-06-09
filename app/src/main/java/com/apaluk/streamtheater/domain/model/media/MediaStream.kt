package com.apaluk.streamtheater.domain.model.media

data class MediaStream(
    val ident: String,
    val size: Long = 0L,
    val duration: Int = 0,
    val speed: Double = 0.0,
    val video: VideoDefinition = VideoDefinition.SD,
    val audios: List<String> = emptyList(),
    val subtitles: List<Subtitles> = emptyList()
)

data class Subtitles(
    val lang: String,
    val forced: Boolean
)

enum class VideoDefinition {
    SD, HD, FHD, UHD_4K, UHD_8K;

    override fun toString(): String =
        when(this) {
            SD -> "SD"
            HD -> "HD"
            FHD -> "FHD"
            UHD_4K -> "4K"
            UHD_8K -> "8K"
        }
}

fun VideoDefinition.height(): Int =
    when(this) {
        VideoDefinition.SD -> 480
        VideoDefinition.HD -> 720
        VideoDefinition.FHD -> 1080
        VideoDefinition.UHD_4K -> 2160
        VideoDefinition.UHD_8K -> 4320
    }

val DUMMY_MEDIA_STREAMS = listOf(
    MediaStream(
        ident = "",
        size = 12345689000L,
        duration = 5420,
        speed = 1204.5,
        video = VideoDefinition.HD,
        audios = listOf("CZ"),
        subtitles = listOf(Subtitles("EN", true))
    ),
    MediaStream(
        ident = "",
        size = 34545689000L,
        duration = 5420,
        speed = 1204.5,
        video = VideoDefinition.FHD,
        audios = emptyList(),
        subtitles = listOf(Subtitles("EN", true), Subtitles("SK", false))
    )
)