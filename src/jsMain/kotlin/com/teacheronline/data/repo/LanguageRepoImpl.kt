package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Language
import com.teacheronline.domain.model.LanguageDto
import com.teacheronline.domain.repo.LanguageRepo
import com.teacheronline.utils.RequestState

class LanguageRepoImpl(private val apiService: ApiService) : LanguageRepo {
    override suspend fun show(id: Long, response: (RequestState<BaseResponse<Language>>) -> Unit) =
        apiService.languageShow(id, response)

    override suspend fun showAll(response: (RequestState<BaseResponse<List<Language>>>) -> Unit) =
        apiService.languagesShowAll(response)

    override suspend fun insert(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) =
        apiService.languageInsert(request, response)

    override suspend fun update(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit) =
        apiService.languageUpdate(request, response)

    override suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.languageDelete(id, response)
}
