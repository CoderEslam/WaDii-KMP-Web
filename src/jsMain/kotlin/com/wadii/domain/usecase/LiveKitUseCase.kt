package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.livekit.LiveKitTokenRequest
import com.wadii.domain.model.call.livekit.LiveKitTokenResponse
import com.wadii.domain.repo.LiveKitRepo
import com.wadii.utils.RequestState

class LiveKitUseCase(private val liveKitRepo: LiveKitRepo) {
    suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    ) = liveKitRepo.getToken(request, response)
}
