package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.serach.SearchModel
import com.wadii.utils.RequestState


interface SearchRepo {
    suspend fun search(
        query: String,
        response: (RequestState<BaseResponse<SearchModel>>) -> Unit
    )
}