package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.serach.SearchModel
import com.wadii.domain.repo.SearchRepo
import com.wadii.utils.RequestState


class SearchUseCase (private val searchRepo: SearchRepo) {

    suspend fun search(
        query: String,
        response: (RequestState<BaseResponse<SearchModel>>) -> Unit
    ) = searchRepo.search(query, response)

}