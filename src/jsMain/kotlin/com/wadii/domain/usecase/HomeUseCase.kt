package com.wadii.domain.usecase


import com.wadii.domain.repo.WadiRepo
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.service.Service
import com.wadii.utils.RequestState


class HomeUseCase(private val repo: WadiRepo) {

    suspend fun login(loginRequest: LoginRequest, response: (RequestState<BaseResponse<User>>) -> Unit) =
        repo.login(loginRequest, response)


    //response
    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit) = repo.getResponseList(response)

    //offers
    suspend fun getOffersList(response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) = repo.getOffersList(response)
    //service
    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit) = repo.getServiceList(response)
}