package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.repo.AdminRepo
import com.wadii.utils.RequestState

class AdminRepoImpl(private val apiService: ApiService) : AdminRepo {
    override suspend fun requests(response: (RequestState<BaseResponse<List<ProviderRequestModel>>>) -> Unit) =
        apiService.requests(response)

    override suspend fun acceptRequest(
        id: Long,
        response: (RequestState<BaseResponse<ProviderModel>>) -> Unit
    ) = apiService.acceptRequest(id, response)

    override suspend fun rejectRequest(
        id: Long,
        response: (RequestState<BaseResponse<ProviderRequestModel>>) -> Unit
    ) = apiService.rejectRequest(id, response)
}