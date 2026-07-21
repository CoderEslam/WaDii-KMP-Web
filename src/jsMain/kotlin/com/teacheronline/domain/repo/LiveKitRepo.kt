package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.call.livekit.LiveKitTokenRequest
import com.teacheronline.domain.model.call.livekit.LiveKitTokenResponse
import com.teacheronline.utils.RequestState

interface LiveKitRepo {
    suspend fun getToken(
        request: LiveKitTokenRequest,
        response: (RequestState<BaseResponse<LiveKitTokenResponse>>) -> Unit
    )
}
