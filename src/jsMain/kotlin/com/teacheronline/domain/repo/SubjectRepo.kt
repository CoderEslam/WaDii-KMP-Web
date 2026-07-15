package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.SubjectRequest
import com.teacheronline.utils.RequestState

interface SubjectRepo {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<Subject>>) -> Unit)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Subject>>>) -> Unit)
    suspend fun insert(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit)
    suspend fun update(request: SubjectRequest, response: (RequestState<BaseResponse<Subject>>) -> Unit)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
