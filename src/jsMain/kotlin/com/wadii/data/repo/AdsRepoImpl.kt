package com.wadii.data.repo


import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.repo.AdsRepo
import com.wadii.utils.RequestState

class AdsRepoImpl(private val apiService: ApiService) : AdsRepo {

    override suspend fun ads(response: (RequestState<BaseResponse<List<Ads>>>) -> Unit) =
        apiService.ads(response)

}