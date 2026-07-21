package com.teacheronline.domain.usecase

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.SecretaryDto
import com.teacheronline.domain.repo.SecretaryRepo
import com.teacheronline.utils.RequestState

class SecretaryUseCase(private val repo: SecretaryRepo) {
    suspend fun create(request: SecretaryDto, response: (RequestState<BaseResponse<Secretary>>) -> Unit) = repo.create(request, response)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Secretary>>>) -> Unit) = repo.showAll(response)
}
