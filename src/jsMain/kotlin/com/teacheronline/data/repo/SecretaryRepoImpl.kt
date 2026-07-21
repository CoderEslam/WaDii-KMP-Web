package com.teacheronline.data.repo

import com.teacheronline.data.api.ApiService
import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.SecretaryDto
import com.teacheronline.domain.repo.SecretaryRepo
import com.teacheronline.utils.RequestState

class SecretaryRepoImpl(private val apiService: ApiService) : SecretaryRepo {
    override suspend fun create(request: SecretaryDto, response: (RequestState<BaseResponse<Secretary>>) -> Unit) =
        apiService.secretaryCreate(request, response)

    override suspend fun showAll(response: (RequestState<BaseResponse<List<Secretary>>>) -> Unit) =
        apiService.secretaryShowAll(response)
}
