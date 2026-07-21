package com.teacheronline.core

import com.teacheronline.data.api.json
import kotlinx.serialization.serializer


fun String.isNotNullOrEmptyString(): Boolean {
    return !(this.isEmpty() || this.isBlank() || this == "null" || this == "")
}

fun String.isNullOrEmptyString(): Boolean {
    return this.isEmpty() || this.isBlank() || this == "null" || this == ""
}

inline fun <reified T> T.toJson(): String {
    return json().encodeToString(serializer<T>(), this)
}


inline fun <reified T> String?.fromJson(): T? {
    return try {
        if (this != null) json().decodeFromString(
            serializer<T>(),
            this
        ) else this
    } catch (e: Exception) {
        println(e.message)
        return null
    }
}

fun String.toInteger(): Int {
    return try {
        this.toIntOrNull() ?: 0
    } catch (e: NumberFormatException) {
        0
    }
}

fun getInitials(fullName: String): String {
    val parts = fullName.trim().split("\\s+".toRegex())
    if (parts.size < 2) return fullName.uppercase()
    val firstInitial = parts.first().first().uppercaseChar()
    val lastInitial = parts.last().first().uppercaseChar()
    return "$firstInitial$lastInitial"
}

