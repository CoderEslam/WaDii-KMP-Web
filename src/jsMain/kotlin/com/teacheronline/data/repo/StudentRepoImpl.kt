package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.StudentRequest
import com.teacheronline.domain.repo.StudentRepo
import com.teacheronline.utils.RequestState

class StudentRepoImpl(private val apiService: ApiService) : StudentRepo {
    override suspend fun create(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentCreate(request, response)

    override suspend fun update(request: StudentRequest, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentUpdate(request, response)

    override suspend fun all(response: (RequestState<BaseResponse<List<Student>>>) -> Unit) =
        apiService.studentsAll(response)

    override suspend fun byId(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentById(id, response)

    override suspend fun byEmail(email: String, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentByEmail(email, response)

    override suspend fun byPhone(phone: String, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentByPhone(phone, response)

    override suspend fun delete(id: Long, response: (RequestState<BaseResponse<Student>>) -> Unit) =
        apiService.studentDelete(id, response)

    override suspend fun byCourse(courseId: Long, response: (RequestState<BaseResponse<List<Student>>>) -> Unit) =
        apiService.studentsByCourse(courseId, response)
}
