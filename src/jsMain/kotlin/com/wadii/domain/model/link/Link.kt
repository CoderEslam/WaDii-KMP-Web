package com.wadii.domain.model.link

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("link")
    val link: String = ""
)