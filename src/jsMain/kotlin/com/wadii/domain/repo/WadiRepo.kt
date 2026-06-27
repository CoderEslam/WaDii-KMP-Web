package com.wadii.domain.repo


import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.auth.login.LoginRequest
import com.wadii.domain.model.auth.login.User
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.response.OrderResponse
import com.wadii.domain.model.service.Service
import com.wadii.utils.RequestState

interface WadiRepo {

    suspend fun login(loginRequest: LoginRequest, response: (RequestState<BaseResponse<User>>) -> Unit)



    //response
    suspend fun getResponseList(response: (RequestState<BaseResponse<List<OrderResponse>>>) -> Unit)

    //offers
    suspend fun getOffersList(response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit)
    //service
    suspend fun getServiceList(response: (RequestState<BaseResponse<List<Service>>>) -> Unit)
}