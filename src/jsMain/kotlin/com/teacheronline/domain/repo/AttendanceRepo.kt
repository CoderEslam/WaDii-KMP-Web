package com.teacheronline.domain.repo

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.utils.RequestState

interface AttendanceRepo {
    suspend fun create(request: AttendanceRequest, response: (RequestState<BaseResponse<Attendance>>) -> Unit)
    suspend fun all(response: (RequestState<BaseResponse<List<Attendance>>>) -> Unit)
    suspend fun markByStudent(studentId: Long, response: (RequestState<BaseResponse<Attendance>>) -> Unit)
}
