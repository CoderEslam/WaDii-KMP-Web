package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Config
import com.teacheronline.domain.model.ConfigDto
import com.teacheronline.domain.repo.ConfigRepo
import com.teacheronline.utils.RequestState

class ConfigUseCase(private val repo: ConfigRepo) {
    suspend fun get(key: String, response: (RequestState<BaseResponse<ConfigDto>>) -> Unit) = repo.get(key, response)
    suspend fun put(key: String, value: String, response: (RequestState<BaseResponse<Config>>) -> Unit) = repo.put(key, value, response)
}
