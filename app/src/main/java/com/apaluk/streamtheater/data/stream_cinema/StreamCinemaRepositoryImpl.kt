package com.apaluk.streamtheater.data.stream_cinema

import com.apaluk.streamtheater.core.util.CacheControl
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.repositoryFlow
import com.apaluk.streamtheater.data.stream_cinema.local.MediaStreamsCache
import com.apaluk.streamtheater.data.stream_cinema.remote.StreamCinemaApi
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.toListOfTvShowChildren
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.toMediaDetail
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.toMediaStream
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.toSearchResultItems
import com.apaluk.streamtheater.domain.model.media.TvShowChild
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamCinemaRepositoryImpl @Inject constructor(
    private val streamCinemaApi: StreamCinemaApi
): StreamCinemaRepository {
    override fun getMediaStreams(mediaId: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.getStreams(mediaId) },
        resultMapping = { it.map { item -> item.toMediaStream() } },
        cacheControl = CacheControl(
            cache = MediaStreamsCache,
            key = mediaId
        )
    )

    override fun search(text: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.search(searchText = text) },
        resultMapping = { it.toSearchResultItems() }
    )

    override fun getMediaDetails(mediaId: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.mediaDetails(mediaId) },
        resultMapping = { it.toMediaDetail() }
    )

    override fun getTvShowChildren(mediaId: String): Flow<Resource<List<TvShowChild>>> = repositoryFlow(
        apiOperation = { streamCinemaApi.getMediaChildren(mediaId = mediaId) },
        resultMapping = { it.toListOfTvShowChildren() }
    )

}