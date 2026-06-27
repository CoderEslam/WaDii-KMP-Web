package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.service.Service
import com.wadii.utils.RequestState

interface ServicesRepo {

    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit)

}