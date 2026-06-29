package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.carTypes.CarType
import com.wadii.domain.model.order.OrderCallbackResponse
import com.wadii.domain.model.order.OrderModel
import com.wadii.domain.model.order.OrderRequest
import com.wadii.domain.repo.OrderRepo
import com.wadii.utils.RequestState


class OrderUseCase(private val orderRepo: OrderRepo) {

    suspend fun showAll(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        orderRepo.showAll(response)

    suspend fun showAllOrderOfUser(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        orderRepo.showAllOrderOfUser(response)

    suspend fun showAllOrderOfProvider(response: (RequestState<BaseResponse<List<OrderModel>>>) -> Unit) =
        orderRepo.showAllOrderOfProvider(response)

    suspend fun insertOrder(
        orderRequest: OrderRequest,
        response: (RequestState<BaseResponse<OrderCallbackResponse>>) -> Unit
    ) = orderRepo.insertOrder(orderRequest, response)

    suspend fun getCarTypeList(response: (RequestState<BaseResponse<List<CarType>>>) -> Unit) =
        orderRepo.getCarTypeList(response)

    suspend fun getOrderById(id: Int, response: (RequestState<BaseResponse<OrderModel>>) -> Unit) =
        orderRepo.getOrderById(id, response)
}