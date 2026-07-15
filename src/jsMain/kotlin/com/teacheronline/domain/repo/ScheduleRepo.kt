package com.teacheronline.domain.repo

import com.teacheronline.domain.model.AttendanceMonth
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.utils.RequestState

interface ScheduleRepo {
    suspend fun create(request: ScheduleDto, response: (RequestState<BaseResponse<Schedule>>) -> Unit)
    suspend fun count(request: AttendanceMonth, response: (RequestState<BaseResponse<Long>>) -> Unit)
}
