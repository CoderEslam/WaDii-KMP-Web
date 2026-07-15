package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.call.livekit.LiveKitTokenRequest
import com.teacheronline.domain.model.call.livekit.LiveKitTokenResponse
import com.teacheronline.domain.repo.LiveKitRepo
import com.teacheronline.utils.RequestState

class LiveKitUseCase(private val liveKitRepo: LiveKitRepo) {
    suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    ) = liveKitRepo.getToken(request, response)
}
