package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.LevelDto
import com.teacheronline.utils.RequestState

interface LevelRepo {
    suspend fun create(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit)
    suspend fun update(request: LevelDto, response: (RequestState<BaseResponse<Level>>) -> Unit)
    suspend fun all(response: (RequestState<BaseResponse<List<Level>>>) -> Unit)
}
