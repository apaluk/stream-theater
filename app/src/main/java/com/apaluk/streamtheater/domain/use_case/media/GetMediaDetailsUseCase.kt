package com.apaluk.streamtheater.domain.use_case.media

import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.domain.model.media.MediaDetail
import com.apaluk.streamtheater.domain.model.media.util.toMediaBrief
import com.apaluk.streamtheater.domain.repository.MediaInfoRepository
import com.apaluk.streamtheater.domain.repository.StreamCinemaRepository
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetMediaDetailsUseCase @Inject constructor(
    private val streamCinemaRepository: StreamCinemaRepository,
    private val mediaInfoRepository: MediaInfoRepository
) {

        suspend operator fun invoke(mediaId: String): Resource<MediaDetail> {
            val mediaDetailResource = streamCinemaRepository.getMediaDetails(mediaId).last()
            if(mediaDetailResource is Resource.Success && mediaDetailResource.data != null) {
                mediaInfoRepository.upsertMediaInfo(mediaDetailResource.data.toMediaBrief())
            }
            return mediaDetailResource
        }
}