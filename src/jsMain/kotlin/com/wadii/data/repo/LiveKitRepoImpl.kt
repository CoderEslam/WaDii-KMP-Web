package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.livekit.LiveKitTokenRequest
import com.wadii.domain.model.call.livekit.LiveKitTokenResponse
import com.wadii.domain.repo.LiveKitRepo
import com.wadii.utils.RequestState

class LiveKitRepoImpl(private val apiService: ApiService) : LiveKitRepo {
    override suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    ) = apiService.getLiveKitToken(request, response)
}
