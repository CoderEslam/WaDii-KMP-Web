package com.wadii.utils

import org.w3c.files.File

/**
 * Client-side validation. Each validator returns an error message, or null when the value is fine.
 * This gives users fast, specific feedback; the backend must still enforce the same rules.
 */
object Validation {
    private val NAME = Regex("^[\\p{L}][\\p{L} .'-]{1,49}$")
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val PHONE = Regex("^\\+?[0-9]{8,15}$")

    const val MAX_UPLOAD_BYTES = 5L * 1024 * 1024
    private val ALLOWED_IMAGE_TYPES = setOf("image/jpeg", "image/png", "image/webp")

    fun name(value: String, label: String = "Name"): String? = when {
        value.isBlank() -> "$label is required"
        !NAME.matches(value.trim()) -> "$label can only contain letters (2–50 characters)"
        else -> null
    }

    fun email(value: String): String? = when {
        value.isBlank() -> "Email is required"
        !EMAIL.matches(value.trim()) -> "Enter a valid email, like you@example.com"
        else -> null
    }

    fun phone(value: String, required: Boolean = true): String? {
        val digits = value.replace(" ", "").replace("-", "")
        return when {
            digits.isBlank() -> if (required) "Phone number is required" else null
            !PHONE.matches(digits) -> "Use digits only, 8–15 numbers (a leading + is fine)"
            else -> null
        }
    }

    fun password(value: String): String? = when {
        value.length < 8 -> "Use at least 8 characters"
        value.none { it.isDigit() } || value.none { it.isLetter() } -> "Mix letters and numbers"
        else -> null
    }

    /** Only http(s) links are accepted, so `javascript:` / `data:` URLs can never be stored or opened. */
    fun url(value: String, required: Boolean = false): String? = when {
        value.isBlank() -> if (required) "Link is required" else null
        !Regex("^https?://[^\\s/$.?#][^\\s]*$", RegexOption.IGNORE_CASE).matches(value.trim()) ->
            "Enter a full link starting with http:// or https://"
        else -> null
    }

    fun image(file: File?, label: String): String? = when {
        file == null -> "$label is required"
        file.type !in ALLOWED_IMAGE_TYPES -> "$label must be a JPG, PNG or WebP image"
        file.size.toDouble() > MAX_UPLOAD_BYTES -> "$label must be smaller than 5 MB"
        else -> null
    }

    /** End must not be before start. Dates are `yyyy-MM-dd`, so string comparison is chronological. */
    fun dateRange(start: String, end: String): String? = when {
        start.isBlank() || end.isBlank() -> "Pick both a start and an end date"
        end < start -> "End date can't be before the start date"
        else -> null
    }
}

/** Marketplace is Egypt-first, so prices are shown in EGP. */
fun formatPrice(amount: Double): String {
    val rounded = kotlin.math.round(amount * 100) / 100
    val text = if (rounded % 1.0 == 0.0) rounded.toLong().toString() else rounded.toString()
    return "EGP $text"
}
