package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EducationalCenter
import com.teacheronline.domain.model.EducationalCenterDto
import com.teacheronline.domain.repo.EducationalCenterRepo
import com.teacheronline.utils.RequestState

class EducationalCenterUseCase(private val repo: EducationalCenterRepo) {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) = repo.show(id, response)
    suspend fun showAll(response: (RequestState<BaseResponse<List<EducationalCenter>>>) -> Unit) = repo.showAll(response)
    suspend fun insert(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) = repo.insert(request, response)
    suspend fun update(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) = repo.update(request, response)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) = repo.delete(id, response)
}
