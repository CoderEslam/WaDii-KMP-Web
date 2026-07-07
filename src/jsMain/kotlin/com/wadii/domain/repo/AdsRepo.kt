package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.ads.InsertAds
import com.wadii.utils.RequestState


interface AdsRepo {
    suspend fun ads(
        response: (RequestState<BaseResponse<List<Ads>>>) -> Unit
    )

    suspend fun insertAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    )

    suspend fun updateAds(
        insertAds: InsertAds,
        response: (RequestState<BaseResponse<Ads>>) -> Unit
    )

    suspend fun deleteAds(
        id: Long,
        response: (RequestState<BaseResponse<Boolean>>) -> Unit
    )
}