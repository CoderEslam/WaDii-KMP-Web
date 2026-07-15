package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

// GET /configs/{key} response shape — value is an Int here.
@Serializable
data class ConfigDto(
    val key: String = "",
    val value: Int = 0
)

// PUT /configs/{key} response shape — value is a String here (doc §9 warns these differ).
@Serializable
data class Config(
    val key: String = "",
    val value: String = ""
)
