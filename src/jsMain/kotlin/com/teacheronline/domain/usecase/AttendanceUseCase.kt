package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.repo.AttendanceRepo
import com.teacheronline.utils.RequestState

class AttendanceUseCase(private val repo: AttendanceRepo) {
    suspend fun create(request: AttendanceRequest, response: (RequestState<BaseResponse<Attendance>>) -> Unit) = repo.create(request, response)
    suspend fun all(response: (RequestState<BaseResponse<List<Attendance>>>) -> Unit) = repo.all(response)
    suspend fun markByStudent(studentId: Long, response: (RequestState<BaseResponse<Attendance>>) -> Unit) = repo.markByStudent(studentId, response)
}
