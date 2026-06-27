package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.providerRequests.ProviderRequestModel
import com.wadii.domain.repo.AdminRepo
import com.wadii.utils.RequestState

class AdminDashboardUseCase(private val adminRepo: AdminRepo) {

    suspend fun requests(response: (RequestState<BaseResponse<List<ProviderRequestModel>>>) -> Unit) =
        adminRepo.requests(response)
}