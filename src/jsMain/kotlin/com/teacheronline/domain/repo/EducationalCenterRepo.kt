package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EducationalCenter
import com.teacheronline.domain.model.EducationalCenterDto
import com.teacheronline.utils.RequestState

interface EducationalCenterRepo {
    suspend fun show(id: Long, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit)
    suspend fun showAll(response: (RequestState<BaseResponse<List<EducationalCenter>>>) -> Unit)
    suspend fun insert(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit)
    suspend fun update(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit)
    suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit)
}
