package com.teacheronline.domain.repo

import com.teacheronline.domain.model.BaseResponse
import com.teacheronline.domain.model.Secretary
import com.teacheronline.domain.model.SecretaryDto
import com.teacheronline.utils.RequestState

interface SecretaryRepo {
    suspend fun create(request: SecretaryDto, response: (RequestState<BaseResponse<Secretary>>) -> Unit)
    suspend fun showAll(response: (RequestState<BaseResponse<List<Secretary>>>) -> Unit)
}
