package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.utils.RequestState


interface ResponseRepo {

    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit)

    suspend fun acceptResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    )

    suspend fun rejectResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    )

    suspend fun createResponse(
        request: ResponseRequest,
        response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit
    )
}