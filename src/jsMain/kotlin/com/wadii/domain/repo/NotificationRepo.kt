package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.utils.RequestState


interface NotificationRepo {
    suspend fun getNotifications(response: (RequestState<BaseResponse<List<String>>>) -> Unit)
    suspend fun notificationCount(response: (RequestState<BaseResponse<Int>>) -> Unit)
}