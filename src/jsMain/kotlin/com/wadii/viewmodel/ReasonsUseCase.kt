package com.wadii.viewmodel

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.InsertReason
import com.wadii.domain.repo.ReasonsRepo
import com.wadii.utils.RequestState

class ReasonsUseCase(private val reasonsRepo: ReasonsRepo) {

    suspend fun getReasonList(response: (RequestState<BaseResponse<List<CancelReason>>>) -> Unit) =
        reasonsRepo.getReasonList(response)

    suspend fun addReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) = reasonsRepo.addReason(insertReason, response)

    suspend fun updateReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    ) = reasonsRepo.updateReason(insertReason, response)

    suspend fun deleteReason(
        id: Int,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = reasonsRepo.deleteReason(id, response)
}
