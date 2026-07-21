package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.call.livekit.LiveKitTokenRequest
import com.teacheronline.domain.model.call.livekit.LiveKitTokenResponse
import com.teacheronline.domain.repo.LiveKitRepo
import com.teacheronline.utils.RequestState

class LiveKitRepoImpl(private val apiService: ApiService) : LiveKitRepo {
    override suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    ) = apiService.getLiveKitToken(request, response)
}
