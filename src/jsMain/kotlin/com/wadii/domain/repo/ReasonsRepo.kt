package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.order.CancelReason
import com.wadii.domain.model.order.InsertReason
import com.wadii.utils.RequestState

interface ReasonsRepo {

    suspend fun getReasonList(response: (RequestState<BaseResponse<List<CancelReason>>>) -> Unit)

    suspend fun addReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    )

    suspend fun updateReason(
        insertReason: InsertReason,
        response: (RequestState<BaseResponse<CancelReason>>) -> Unit
    )

    suspend fun deleteReason(id: Int, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
