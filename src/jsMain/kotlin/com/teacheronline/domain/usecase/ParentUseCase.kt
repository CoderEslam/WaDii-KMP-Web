package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EnrollRequest
import com.teacheronline.domain.model.Parent
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.repo.ParentRepo
import com.teacheronline.utils.RequestState

class ParentUseCase(private val repo: ParentRepo) {
    suspend fun enroll(request: EnrollRequest, response: (RequestState<BaseResponse<Parent>>) -> Unit) = repo.enroll(request, response)
    suspend fun addStudents(parentId: Long, students: List<StudentRequest>, response: (RequestState<BaseResponse<Parent>>) -> Unit) =
        repo.addStudents(parentId, students, response)
    suspend fun all(response: (RequestState<BaseResponse<List<Parent>>>) -> Unit) = repo.all(response)
}
