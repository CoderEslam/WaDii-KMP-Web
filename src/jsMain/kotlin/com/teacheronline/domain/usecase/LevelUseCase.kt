package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.LevelDto
import com.teacheronline.domain.repo.LevelRepo
import com.teacheronline.utils.RequestState

class LevelUseCase(private val repo: LevelRepo) {
    suspend fun create(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) = repo.create(request, response)
    suspend fun update(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit) = repo.update(request, response)
    suspend fun all(response: (RequestState<BaseResponse<List<Level>>>) -> Unit) = repo.all(response)
}
