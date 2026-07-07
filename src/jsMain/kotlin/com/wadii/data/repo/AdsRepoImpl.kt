package com.wadii.data.repo


import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.domain.repo.AdsRepo
import com.wadii.utils.RequestState

class AdsRepoImpl(private val apiService: ApiService) : AdsRepo {

    override suspend fun ads(response: (RequestState<BaseResponse<List<Ads>>>) -> Unit) =
        apiService.ads(response)

    override suspend fun insertAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) = apiService.insertAds(insertAds, response)

    override suspend fun updateAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) = apiService.updateAds(insertAds, response)

    override suspend fun deleteAds(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = apiService.deleteAds(id, response)

}