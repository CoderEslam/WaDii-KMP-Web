package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.livekit.LiveKitTokenRequest
import com.wadii.domain.model.call.livekit.LiveKitTokenResponse
import com.wadii.utils.RequestState

interface LiveKitRepo {
    suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    )
}
