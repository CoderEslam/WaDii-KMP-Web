package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.domain.repo.AdsRepo
import com.wadii.utils.RequestState


class AdsUseCase(private val adsRepo: AdsRepo) {

    suspend fun ads(
        response: (RequestState<BaseResponse<List<Ads>>>) -> Unit
    ) = adsRepo.ads(response)

    suspend fun insertAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) = adsRepo.insertAds(insertAds, response)

    suspend fun updateAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    ) = adsRepo.updateAds(insertAds, response)

    suspend fun deleteAds(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    ) = adsRepo.deleteAds(id, response)

}