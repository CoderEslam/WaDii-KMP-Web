package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EnrollRequest
import com.teacheronline.domain.model.Parent
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.utils.RequestState

interface ParentRepo {
    suspend fun enroll(request: EnrollRequest, response: (RequestState<BaseResponse<Parent>>) -> Unit)
    suspend fun addStudents(parentId: Long, students: List<StudentRequest>, response: (RequestState<BaseResponse<Parent>>) -> Unit)
    suspend fun all(response: (RequestState<BaseResponse<List<Parent>>>) -> Unit)
}
