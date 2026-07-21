package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EnrollRequest
import com.teacheronline.domain.model.Parent
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.repo.ParentRepo
import com.teacheronline.utils.RequestState

class ParentRepoImpl(private val apiService: ApiService) : ParentRepo {
    override suspend fun enroll(request: EnrollRequest, response: (RequestState<BaseResponse<Parent>>) -> Unit) =
        apiService.parentEnroll(request, response)

    override suspend fun addStudents(parentId: Long, students: List<StudentRequest>, response: (RequestState<BaseResponse<Parent>>) -> Unit) =
        apiService.parentAddStudents(parentId, students, response)

    override suspend fun all(response: (RequestState<BaseResponse<List<Parent>>>) -> Unit) =
        apiService.parentsAll(response)
}
