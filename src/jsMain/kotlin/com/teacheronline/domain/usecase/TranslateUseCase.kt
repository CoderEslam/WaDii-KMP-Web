package com.teacheronline.domain.usecase

import com.teacheronline.domain.repo.TranslateRepo

class TranslateUseCase(private val repo: TranslateRepo) {
    suspend fun translate(text: String, targetLanguage: String): String = repo.translate(text, targetLanguage)
}
