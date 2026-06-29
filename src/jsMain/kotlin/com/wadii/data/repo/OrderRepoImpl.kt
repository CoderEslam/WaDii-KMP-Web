package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.order.OrderCallbackResponse
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.repo.OrderRepo
import com.wadii.utils.RequestState


class OrderRepoImpl(private val apiService: ApiService) : OrderRepo {

    override suspend fun showAll(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        apiService.showAll(response)

    override suspend fun showAllOrderOfUser(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        apiService.showAllOrderOfUser(response)

    override suspend fun showAllOrderOfProvider(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        apiService.showAllOrderOfProvider(response)

    override suspend fun insertOrder(
        orderRequest: OrderRequest,
        response: (RequestState<BaseResponse<OrderCallbackResponse>>) -> Unit
    ) = apiService.insertOrder(orderRequest, response)

    override suspend fun getCarTypeList(response: (RequestState<BaseResponse<List<CarType>>>) -> Unit) =
        apiService.getCarTypeList(response)

    override suspend fun getOrderById(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        apiService.getOrderById(id, response)

}