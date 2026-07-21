package com.teacheronline.domain.model.auth

import kotlinx.serialization.Serializable

@Serializable
enum class Role { ADMIN, TEACHER, STUDENT, PARENTS, SECRETARY }
