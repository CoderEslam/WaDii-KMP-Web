package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.domain.model.chat.InsertMessage
import com.teacheronline.domain.model.chat.InsertResponse
import com.teacheronline.domain.model.chat.PageMessages
import com.teacheronline.domain.model.chat.ShowAllMessagesResponse
import com.teacheronline.domain.repo.MessagesRepo
import com.teacheronline.utils.RequestState


class MessagesRepoImpl(private val apiService: ApiService) : MessagesRepo {

    override suspend fun chatList(response: (RequestState<BaseResponse<List<ChatContact>>>) -> Unit) =
        apiService.chatList(response)

    override suspend fun insertMessage(
        insertMessage: InsertMessage,
        response: (RequestState<BaseResponse<InsertResponse>>) -> Unit
    ) = apiService.insertMessage(insertMessage, response)

    override suspend fun showAllMessages(response: (RequestState<BaseResponse<List<ShowAllMessagesResponse>>>) -> Unit) =
        apiService.showAllMessages(response)

    override suspend fun conversation(
        userId: Long,
        page: Int,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    ) = apiService.conversation(userId = userId, page = page, response)
}