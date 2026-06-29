package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.order.OrderCallbackResponse
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.utils.RequestState


interface OrderRepo {

    suspend fun showAll(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit)

    suspend fun showAllOrderOfUser(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit)
    suspend fun showAllOrderOfProvider(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit)

    suspend fun insertOrder(
        orderRequest: OrderRequest,
        response: (RequestState<BaseResponse<OrderCallbackResponse>>) -> Unit
    )

    suspend fun getCarTypeList(response: (RequestState<BaseResponse<List<CarType>>>) -> Unit)

    suspend fun getOrderById(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit)
}