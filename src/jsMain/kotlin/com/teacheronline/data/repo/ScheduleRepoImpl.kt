package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.AttendanceMonth
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.domain.repo.ScheduleRepo
import com.teacheronline.utils.RequestState

class ScheduleRepoImpl(private val apiService: ApiService) : ScheduleRepo {
    override suspend fun create(request: ScheduleDto, response: (RequestState<BaseResponse<Schedule>>) -> Unit) =
        apiService.scheduleCreate(request, response)

    override suspend fun count(request: AttendanceMonth, response: (RequestState<BaseResponse<Long>>) -> Unit) =
        apiService.scheduleCount(request, response)
}
