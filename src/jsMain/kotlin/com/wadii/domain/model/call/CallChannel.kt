package com.wadii.domain.model.call

object CallChannel {
    fun name(userId: Int, contactId: Int): String =
        listOf(userId, contactId).sorted().joinToString("_")
}
