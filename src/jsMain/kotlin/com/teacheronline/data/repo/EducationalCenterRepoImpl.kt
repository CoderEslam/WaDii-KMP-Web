package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.EducationalCenter
import com.teacheronline.domain.model.EducationalCenterDto
import com.teacheronline.domain.repo.EducationalCenterRepo
import com.teacheronline.utils.RequestState

class EducationalCenterRepoImpl(private val apiService: ApiService) : EducationalCenterRepo {
    override suspend fun show(id: Long, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) =
        apiService.centerShow(id, response)

    override suspend fun showAll(response: (RequestState<BaseResponse<List<EducationalCenter>>>) -> Unit) =
        apiService.centersShowAll(response)

    override suspend fun insert(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) =
        apiService.centerInsert(request, response)

    override suspend fun update(request: EducationalCenterDto, response: (RequestState<BaseResponse<EducationalCenter>>) -> Unit) =
        apiService.centerUpdate(request, response)

    override suspend fun delete(id: Long, response: (RequestState<BaseResponse<Boolean>>) -> Unit) =
        apiService.centerDelete(id, response)
}
