package com.apaluk.streamtheater.domain.use_case.webshare

import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.data.webshare.WebShareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFileLinkUseCase @Inject constructor(
    private val loginManager: LoginManager,
    private val webShareRepository: WebShareRepository
) {

    suspend operator fun invoke(fileIdent: String): Flow<Resource<String>> =
        webShareRepository.getFileLink(loginManager.getWebShareToken(), fileIdent)

}