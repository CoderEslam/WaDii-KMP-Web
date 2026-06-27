package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.utils.RequestState


interface AdsRepo {
    suspend fun ads(
        response: (RequestState<BaseResponse<List<Ads>>>) -> Unit
    )
}