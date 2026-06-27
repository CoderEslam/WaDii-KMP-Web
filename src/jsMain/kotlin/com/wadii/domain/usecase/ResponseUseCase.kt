package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.domain.repo.ResponseRepo
import com.wadii.utils.RequestState


class ResponseUseCase(private val responseRepo: ResponseRepo) {

    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit) =
        responseRepo.getResponseList(response)

    suspend fun acceptResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) = responseRepo.acceptResponse(request, response)

    suspend fun rejectResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) = responseRepo.rejectResponse(request, response)

    suspend fun createResponse(
        request: ResponseRequest,
        response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit
    ) = responseRepo.createResponse(request, response)
}