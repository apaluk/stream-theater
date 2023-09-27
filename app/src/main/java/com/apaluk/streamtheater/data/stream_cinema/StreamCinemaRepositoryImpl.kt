package com.apaluk.streamtheater.data.stream_cinema

import com.apaluk.streamtheater.core.util.CacheControl
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.repositoryFlow
import com.apaluk.streamtheater.data.stream_cinema.local.MediaStreamsCache
import com.apaluk.streamtheater.data.stream_cinema.remote.StreamCinemaApi
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.StreamCinemaMediaMapper
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.StreamCinemaSearchMapper
import com.apaluk.streamtheater.data.stream_cinema.remote.mapper.StreamCinemaStreamsMapper
import com.apaluk.streamtheater.domain.model.media.TvShowChild
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamCinemaRepositoryImpl @Inject constructor(
    private val streamCinemaApi: StreamCinemaApi,
    private val mediaMapper: StreamCinemaMediaMapper,
    private val searchMapper: StreamCinemaSearchMapper,
    private val streamsMapper: StreamCinemaStreamsMapper
): StreamCinemaRepository {
    override fun getMediaStreams(mediaId: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.getStreams(mediaId) },
        resultMapping = { it.map { item -> streamsMapper.toMediaStream(item) } },
        cacheControl = CacheControl(
            cache = MediaStreamsCache,
            key = mediaId
        )
    )

    override fun search(text: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.search(searchText = text) },
        resultMapping = { searchMapper.toSearchResultItems(it) }
    )

    override fun getMediaDetails(mediaId: String) = repositoryFlow(
        apiOperation = { streamCinemaApi.mediaDetails(mediaId) },
        resultMapping = { mediaMapper.toMediaDetail(it) }
    )

    override fun getTvShowChildren(mediaId: String): Flow<Resource<List<TvShowChild>>> = repositoryFlow(
        apiOperation = { streamCinemaApi.getMediaChildren(mediaId = mediaId) },
        resultMapping = { mediaMapper.toListOfTvShowChildren(it) }
    )

}