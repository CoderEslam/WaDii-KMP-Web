package com.wadii.data.repo

import com.wadii.domain.repo.SearchRepo
import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.serach.SearchModel
import com.wadii.utils.RequestState


class SearchRepoImpl(private val apiService: ApiService) : SearchRepo {

    override suspend fun search(
        query: String,
        response: (RequestState<BaseResponse<SearchModel>>) -> Unit
    ) = apiService.search(query, response)

}