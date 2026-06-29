package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.chat.ChatContact
import com.wadii.domain.model.chat.InsertMessage
import com.wadii.domain.model.chat.InsertResponse
import com.wadii.domain.model.chat.PageMessages
import com.wadii.domain.model.chat.ShowAllMessagesResponse
import com.wadii.domain.repo.MessagesRepo
import com.wadii.utils.RequestState


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
        userId: Int,
        page: Int = 0,
        response: (RequestState<BaseResponse<PageMessages>>) -> Unit
    ) = messageRepo.conversation(
        userId = userId,
        page = page,
        response
    )

}