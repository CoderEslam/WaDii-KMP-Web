package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Language
import com.teacheronline.domain.model.LanguageDto
import com.teacheronline.utils.RequestState

interface LanguageRepo {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Language>>) -> Unit)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Language>>>) -> Unit)
    suspend fun insert(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit)
    suspend fun update(request: LanguageDto, response: (RequestState<BaseResponse<Language>>) -> Unit)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
