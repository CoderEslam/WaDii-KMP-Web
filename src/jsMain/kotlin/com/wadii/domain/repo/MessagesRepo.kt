package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.InsertResponse
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.model.chat.ShowAllMessagesResponse
import com.wadii.utils.RequestState


interface MessagesRepo {

    suspend fun chatList(response: (RequestState<BaseResponse<List<ChatContact>>>) -> Unit)

    suspend fun insertMessage(
        insertMessage: InsertMessage,
        response: (RequestState<BaseResponse<InsertResponse>>) -> Unit
    )

    suspend fun showAllMessages(response: (RequestState<BaseResponse<List<ShowAllMessagesResponse>>>) -> Unit)

    suspend fun conversation(
        userId: Long,
        page: Int = 0,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    )
}