package com.teacheronline.domain.repo

interface TranslateRepo {
    suspend fun translate(text: String, targetLanguage: String): String
}
