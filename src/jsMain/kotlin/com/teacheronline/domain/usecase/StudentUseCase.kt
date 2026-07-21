package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.repo.StudentRepo
import com.teacheronline.utils.RequestState

class StudentUseCase(private val repo: StudentRepo) {
    suspend fun create(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.create(request, response)
    suspend fun update(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.update(request, response)
    suspend fun all(response: (RequestState<BaseResponse<List<Student>>>) -> Unit) = repo.all(response)
    suspend fun byId(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.byId(id, response)
    suspend fun byEmail(email: String, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.byEmail(email, response)
    suspend fun byPhone(phone: String, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.byPhone(phone, response)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) = repo.delete(id, response)
    suspend fun byCourse(courseId: Long, response: (RequestState<BaseResponse<List<Student>>>) -> Unit) = repo.byCourse(courseId, response)
}
