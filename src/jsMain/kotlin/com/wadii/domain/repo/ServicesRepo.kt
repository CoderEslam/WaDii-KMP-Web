package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.service.InsertService
import com.wadii.domain.model.service.Service
import com.wadii.utils.RequestState

interface ServicesRepo {

    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit)

    suspend fun addService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    )

    suspend fun updateService(
        insertService: InsertService,
        response: (RequestState<BaseResponse<Service>>) -> Unit
    )

    suspend fun deleteService(id: Long, response: (RequestState<BaseResponse<Service>>) -> Unit)
}