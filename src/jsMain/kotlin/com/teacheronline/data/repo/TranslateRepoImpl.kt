package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.repo.TranslateRepo

class TranslateRepoImpl(private val apiService: ApiService) : TranslateRepo {
    override suspend fun translate(text: String, targetLanguage: String): String =
        apiService.translate(text, targetLanguage)
}
