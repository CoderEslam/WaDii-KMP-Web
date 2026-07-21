package com.teacheronline.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id: Long = 0,
    val name: String = "",
    val students: List<Student> = emptyList()
)

@Serializable
data class LevelDto(
    val id: Long? = null,
    val name: String = ""
)
