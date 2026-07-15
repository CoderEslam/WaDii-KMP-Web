package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.repo.AttendanceRepo
import com.teacheronline.utils.RequestState

class AttendanceRepoImpl(private val apiService: ApiService) : AttendanceRepo {
    override suspend fun create(request: AttendanceRequest, response: (RequestState<BaseResponse<Attendance>>) -> Unit) =
        apiService.attendanceCreate(request, response)

    override suspend fun all(response: (RequestState<BaseResponse<List<Attendance>>>) -> Unit) =
        apiService.attendanceAll(response)

    override suspend fun markByStudent(studentId: Long, response: (RequestState<BaseResponse<Attendance>>) -> Unit) =
        apiService.attendanceMarkByStudent(studentId, response)
}
