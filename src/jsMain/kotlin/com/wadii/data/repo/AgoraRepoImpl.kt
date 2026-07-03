package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.agora.AgoraTokenRequest
import com.wadii.domain.model.call.agora.AgoraTokenResponse
import com.wadii.domain.repo.AgoraRepo
import com.wadii.utils.RequestState

class AgoraRepoImpl(private val apiService: ApiService) : AgoraRepo {
    override suspend fun getToken(
        request: AgoraTokenRequest,
        response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit
    ) = apiService.getAgoraToken(request, response)
}
