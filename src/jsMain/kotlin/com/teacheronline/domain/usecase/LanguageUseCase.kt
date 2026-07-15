package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Language
import com.teacheronline.domain.model.LanguageDto
import com.teacheronline.domain.repo.LanguageRepo
import com.teacheronline.utils.RequestState

class LanguageUseCase(private val repo: LanguageRepo) {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Language>>) -> Unit) = repo.show(id, response)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Language>>>) -> Unit) = repo.showAll(response)
    suspend fun insert(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) = repo.insert(request, response)
    suspend fun update(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) = repo.update(request, response)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) = repo.delete(id, response)
}
