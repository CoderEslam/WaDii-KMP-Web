package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.call.agora.AgoraTokenRequest
import com.wadii.domain.model.call.agora.AgoraTokenResponse
import com.wadii.domain.repo.AgoraRepo
import com.wadii.utils.RequestState

class AgoraUseCase(private val agoraRepo: AgoraRepo) {
    suspend fun getToken(
        request: AgoraTokenRequest,
        response: (RequestState<BaseResponse<AgoraTokenResponse>>) -> Unit
    ) = agoraRepo.getToken(request, response)
}
