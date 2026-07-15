package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.AttendanceMonth
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Schedule
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.domain.repo.ScheduleRepo
import com.teacheronline.utils.RequestState

class ScheduleUseCase(private val repo: ScheduleRepo) {
    suspend fun create(request: ScheduleDto, response: (RequestState<BaseResponse<Schedule>>) -> Unit) = repo.create(request, response)
    suspend fun count(request: AttendanceMonth, response: (RequestState<BaseResponse<Long>>) -> Unit) = repo.count(request, response)
}
