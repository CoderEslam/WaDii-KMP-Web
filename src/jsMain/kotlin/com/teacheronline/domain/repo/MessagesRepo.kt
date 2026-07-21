package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.domain.model.chat.InsertMessage
import com.teacheronline.domain.model.chat.InsertResponse
import com.teacheronline.domain.model.chat.PageMessages
import com.teacheronline.domain.model.chat.ShowAllMessagesResponse
import com.teacheronline.utils.RequestState


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