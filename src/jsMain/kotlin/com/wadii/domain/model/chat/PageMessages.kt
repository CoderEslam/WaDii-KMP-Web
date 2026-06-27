package com.wadii.domain.model.chat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PageMessages(
    @SerialName("content")
    val content: List<Content> = listOf(),
    @SerialName("empty")
    val empty: Boolean = false,
    @SerialName("first")
    val first: Boolean = false,
    @SerialName("last")
    val last: Boolean = false,
    @SerialName("number")
    val number: Int = 0,
    @SerialName("numberOfElements")
    val numberOfElements: Int = 0,
    @SerialName("pageable")
    val pageable: Pageable = Pageable(),
    @SerialName("size")
    val size: Int = 0,
    @SerialName("sort")
    val sort: Sort = Sort(),
    @SerialName("totalElements")
    val totalElements: Int = 0,
    @SerialName("totalPages")
    val totalPages: Int = 0
) {
    @Serializable
    data class Content(
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("fromUser")
        val fromUser: User = User(),
        @SerialName("id")
        val id: Int = 0,
        @SerialName("text")
        val text: String = "",
        @SerialName("toUser")
        val toUser: User = User(),
        @SerialName("type")
        val type: String = ""
    ) {
        @Serializable
        data class User(
            @SerialName("email")
            val email: String = "",
            @SerialName("fcmToken")
            val fcmToken: String = "",
            @SerialName("firstName")
            val firstName: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("image")
            val image: String? = null,
            @SerialName("lastName")
            val lastName: String = "",
            @SerialName("phone")
            val phone: String = "",
            @SerialName("role")
            val role: String = ""
        )

    }

    @Serializable
    data class Pageable(
        @SerialName("offset")
        val offset: Int = 0,
        @SerialName("pageNumber")
        val pageNumber: Int = 0,
        @SerialName("pageSize")
        val pageSize: Int = 0,
        @SerialName("paged")
        val paged: Boolean = false,
        @SerialName("sort")
        val sort: Sort = Sort(),
        @SerialName("unpaged")
        val unpaged: Boolean = false
    ) {
        @Serializable
        data class Sort(
            @SerialName("empty")
            val empty: Boolean = false,
            @SerialName("sorted")
            val sorted: Boolean = false,
            @SerialName("unsorted")
            val unsorted: Boolean = false
        )
    }

    @Serializable
    data class Sort(
        @SerialName("empty")
        val empty: Boolean = false,
        @SerialName("sorted")
        val sorted: Boolean = false,
        @SerialName("unsorted")
        val unsorted: Boolean = false
    )
}
