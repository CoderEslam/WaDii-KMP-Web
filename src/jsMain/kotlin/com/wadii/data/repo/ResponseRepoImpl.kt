package com.wadii.data.repo


import com.wadii.domain.repo.ResponseRepo
import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.response.ResponseCallback
import com.wadii.domain.model.response.ResponseRequest
import com.wadii.utils.RequestState

class ResponseRepoImpl(private val apiService: ApiService) : ResponseRepo {
    override suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit) =
        apiService.getResponseList(response)

    override suspend fun acceptResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) = apiService.acceptResponse(request, response)

    override suspend fun rejectResponse(
        request: OrderResponse,
        response: (RequestState<BaseResponse<OrderResponse>>) -> Unit
    ) = apiService.rejectResponse(request, response)

    override suspend fun createResponse(
        request: ResponseRequest,
        response: (RequestState<BaseResponse<ResponseCallback>>) -> Unit
    ) = apiService.createResponse(request, response)
}