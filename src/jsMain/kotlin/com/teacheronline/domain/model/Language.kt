package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val id: Long = 0,
    val name: String = "",
    val prefix: String = ""
)

@Serializable
data class LanguageDto(
    val id: Long? = null,
    val name: String = "",
    val prefix: String = ""
)
