package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Config
import com.teacheronline.domain.model.ConfigDto
import com.teacheronline.utils.RequestState

interface ConfigRepo {
    suspend fun get(key: String, response: (RequestState<BaseResponse<ConfigDto>>) -> Unit)
    suspend fun put(key: String, value: String, response: (RequestState<BaseResponse<Config>>) -> Unit)
}
