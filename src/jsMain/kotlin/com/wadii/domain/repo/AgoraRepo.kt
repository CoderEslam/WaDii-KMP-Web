package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.agora.AgoraTokenRequest
import com.wadii.domain.model.call.agora.AgoraTokenResponse
import com.wadii.utils.RequestState

interface AgoraRepo {
    suspend fun getToken(
        request: AgoraTokenRequest,
        response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit
    )
}
