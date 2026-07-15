package com.teacheronline.domain.model.chat


import kotlinx.serialization.Serializable

@Serializable
data class Presence(
    val online: Boolean,
    val userId: Int
)