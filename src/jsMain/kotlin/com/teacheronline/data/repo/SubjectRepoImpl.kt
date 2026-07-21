package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.SubjectRequest
import com.teacheronline.domain.repo.SubjectRepo
import com.teacheronline.utils.RequestState

class SubjectRepoImpl(private val apiService: ApiService) : SubjectRepo {
    override suspend fun show(id: Long, response: (RequestState<BaseResponse<Subject>>) -> Unit) =
        apiService.subjectShow(id, response)

    override suspend fun showAll(response: (RequestState<BaseResponse<List<Subject>>>) -> Unit) =
        apiService.subjectsShowAll(response)

    override suspend fun insert(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) =
        apiService.subjectInsert(request, response)

    override suspend fun update(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit) =
        apiService.subjectUpdate(request, response)

    override suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.subjectDelete(id, response)
}
