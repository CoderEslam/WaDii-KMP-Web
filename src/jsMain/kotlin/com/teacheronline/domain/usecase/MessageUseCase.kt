package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.chat.ChatContact
import com.teacheronline.domain.model.chat.InsertMessage
import com.teacheronline.domain.model.chat.InsertResponse
import com.teacheronline.domain.model.chat.PageMessages
import com.teacheronline.domain.model.chat.ShowAllMessagesResponse
import com.teacheronline.domain.repo.MessagesRepo
import com.teacheronline.utils.RequestState


class MessageUseCase(private val messageRepo: MessagesRepo) {

    suspend fun chatList(response: (RequestState<BaseResponse<List<ChatContact>>>) -> Unit) =
        messageRepo.chatList(response)

    suspend fun insertMessage(
        insertMessage: InsertMessage,
        response: (RequestState<BaseResponse<InsertResponse>>) -> Unit
    ) = messageRepo.insertMessage(insertMessage, response)

    suspend fun showAllMessages(response: (RequestState<BaseResponse<List<ShowAllMessagesResponse>>>) -> Unit) =
        messageRepo.showAllMessages(response)

    suspend fun conversation(
        userId: Long,
        page: Int = 0,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    ) = messageRepo.conversation(
        userId = userId,
        page = page,
        response
    )

}