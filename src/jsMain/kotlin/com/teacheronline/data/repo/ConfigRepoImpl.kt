package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Config
import com.teacheronline.domain.model.ConfigDto
import com.teacheronline.domain.repo.ConfigRepo
import com.teacheronline.utils.RequestState

class ConfigRepoImpl(private val apiService: ApiService) : ConfigRepo {
    override suspend fun get(key: String, response: (RequestState<BaseResponse<ConfigDto>>) -> Unit) =
        apiService.configGet(key, response)

    override suspend fun put(key: String, value: String, response: (RequestState<BaseResponse<Config>>) -> Unit) =
        apiService.configPut(key, value, response)
}
