package com.apaluk.streamtheater.data.stream_cinema.remote

import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.media.MediaDetailDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.search.SearchResponseDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.streams.MediaStreamsResponseItemDto
import com.apaluk.streamtheater.data.stream_cinema.remote.dto.tv_show.children.MediaChildrenResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StreamCinemaApi {

    @GET("/api/media/{id}")
    suspend fun mediaDetails(
        @Path("id") mediaId: String,
        @Query("access_token") accessToken: String = Constants.STREAM_CINEMA_ACCESS_TOKEN,
    ): Response<MediaDetailDto>

    @GET("/api/media/{id}/streams")
    suspend fun getStreams(
        @Path("id") mediaId: String,
        @Query("access_token") accessToken: String = Constants.STREAM_CINEMA_ACCESS_TOKEN,
    ): Response<List<MediaStreamsResponseItemDto>>

    @GET("/api/media/filter/v2/search")
    suspend fun search(
        @Query("access_token") accessToken: String = Constants.STREAM_CINEMA_ACCESS_TOKEN,
        @Query("type") type: String = "*",
        @Query("order") order: String = "desc",
        @Query("value") searchText: String
    ): Response<SearchResponseDto>

    @GET("/api/media/filter/v2/parent")
    suspend fun getMediaChildren(
        @Query("access_token") accessToken: String = Constants.STREAM_CINEMA_ACCESS_TOKEN,
        @Query("value") mediaId: String,
        @Query("type") type: String = "season"
    ): Response<MediaChildrenResponseDto>

}