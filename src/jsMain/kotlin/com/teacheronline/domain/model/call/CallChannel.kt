package com.teacheronline.domain.model.call

object CallChannel {
    fun name(userId: Long, contactId: Long): String =
        listOf(userId, contactId).sorted().joinToString("_")
}
