package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.utils.RequestState

interface StudentRepo {
    suspend fun create(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun update(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun all(response: (RequestState<BaseResponse<List<Student>>>) -> Unit)
    suspend fun byId(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun byEmail(email: String, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun byPhone(phone: String, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit)
    suspend fun byCourse(courseId: Long, response: (RequestState<BaseResponse<List<Student>>>) -> Unit)
}
