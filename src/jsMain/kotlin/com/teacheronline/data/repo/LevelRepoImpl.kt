package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.LevelDto
import com.teacheronline.domain.repo.LevelRepo
import com.teacheronline.utils.RequestState

class LevelRepoImpl(private val apiService: ApiService) : LevelRepo {
    override suspend fun create(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) =
        apiService.levelCreate(request, response)

    override suspend fun update(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) =
        apiService.levelUpdate(request, response)

    override suspend fun all(response: (RequestState<BaseResponse<List<Level>>>) -> Unit) =
        apiService.levelsAll(response)
}
