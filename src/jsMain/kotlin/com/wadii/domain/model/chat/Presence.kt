package com.wadii.domain.model.chat


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Presence(
    val online: Boolean,
    val userId: Int
)