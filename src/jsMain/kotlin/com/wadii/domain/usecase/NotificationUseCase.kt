package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.repo.NotificationRepo
import com.wadii.utils.RequestState


class NotificationUseCase(private val notificationRepo: NotificationRepo) {
    suspend fun getNotifications(response: (RequestState<BaseResponse<List<String>>>) -> Unit) =
        notificationRepo.getNotifications(response)

    suspend fun notificationCount(response: (RequestState<BaseResponse<Int>>) -> Unit) =
        notificationRepo.notificationCount(response)
}