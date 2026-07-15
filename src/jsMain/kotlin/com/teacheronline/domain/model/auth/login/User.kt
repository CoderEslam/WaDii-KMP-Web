package com.teacheronline.domain.model.auth.login

import com.teacheronline.domain.model.EducationalCenter
import com.teacheronline.domain.model.auth.Role
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("firstName")
    val firstName: String = "",
    @SerialName("lastName")
    val lastName: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("fcmToken")
    val fcmToken: String = "",
    @SerialName("token")
    val token: String? = null,
    @SerialName("role")
    val role: Role = Role.ADMIN,
    @SerialName("educationalCenters")
    val educationalCenters: List<EducationalCenter> = emptyList()
) {
    val fullName: String get() = "$firstName $lastName".trim()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false
        other as User
        return id == other.id
    }

    override fun hashCode(): Int = id.toInt()
}
