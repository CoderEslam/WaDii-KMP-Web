package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.service.Service
import com.wadii.domain.repo.ServicesRepo
import com.wadii.utils.RequestState

class ServicesRepoImpl(private val apiService: ApiService) : ServicesRepo {


    override suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit) =
        apiService.getServiceList(response)


}