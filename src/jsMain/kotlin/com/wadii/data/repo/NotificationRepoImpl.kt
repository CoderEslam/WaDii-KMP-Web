package com.wadii.data.repo


import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.repo.NotificationRepo
import com.wadii.utils.RequestState

class NotificationRepoImpl(private val apiService: ApiService) : NotificationRepo {

    override suspend fun getNotifications(response: (RequestState<BaseResponse<List<String>>>) -> Unit) =
        apiService.getNotifications(response)

    override suspend fun notificationCount(response: (RequestState<BaseResponse<Int>>) -> Unit) =
        apiService.notificationCount(response)

}