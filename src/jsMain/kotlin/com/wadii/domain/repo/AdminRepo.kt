package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.utils.RequestState

interface AdminRepo {

    suspend fun requests(
        response: (RequestState<BaseResponse<List<ProviderRequestModel>>>) -> Unit
    )

    suspend fun acceptRequest(
        id: Long,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    )

    suspend fun rejectRequest(
        id: Long,
        response: (RequestState<BaseResponse<ProviderRequestModel>>) -> Unit
    )

}