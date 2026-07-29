package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.InsertReason
import com.wadii.domain.repo.ReasonsRepo
import com.wadii.utils.RequestState

class ReasonsRepoImpl(private val apiService: ApiService) : ReasonsRepo {

    override suspend fun getReasonList(response: (RequestState<BaseResponse<List<CancelReason>>>) -> Unit) =
        apiService.getCancelReasons(response)

    override suspend fun addReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) = apiService.addReason(insertReason, response)

    override suspend fun updateReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) = apiService.updateReason(insertReason, response)

    override suspend fun deleteReason(
        id: Int,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.deleteReason(id, response)

}
