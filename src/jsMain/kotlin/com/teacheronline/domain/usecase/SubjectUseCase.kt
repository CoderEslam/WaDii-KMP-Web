package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.SubjectRequest
import com.teacheronline.domain.repo.SubjectRepo
import com.teacheronline.utils.RequestState

class SubjectUseCase(private val repo: SubjectRepo) {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Subject>>) -> Unit) = repo.show(id, response)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Subject>>>) -> Unit) = repo.showAll(response)
    suspend fun insert(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) = repo.insert(request, response)
    suspend fun update(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) = repo.update(request, response)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) = repo.delete(id, response)
}
