package com.wadii.viewmodel

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.service.InsertService
import com.wadii.domain.model.service.Service
import com.wadii.domain.repo.ServicesRepo
import com.wadii.utils.RequestState

class ServicesUseCase(private val servicesRepo: ServicesRepo) {

    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit) =
        servicesRepo.getServiceList(response)

    suspend fun addService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    ) = servicesRepo.addService(insertService, response)

    suspend fun updateService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    ) = servicesRepo.updateService(insertService, response)

    suspend fun deleteService(
        id: Long,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    ) = servicesRepo.deleteService(id, response)
}