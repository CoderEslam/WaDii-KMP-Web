package com.wadii.viewmodel

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.service.Service
import com.wadii.domain.repo.ServicesRepo
import com.wadii.utils.RequestState

class ServicesUseCase(private val servicesRepo: ServicesRepo) {
    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit) =
        servicesRepo.getServiceList(response)
}