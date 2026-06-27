package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.repo.AdsRepo
import com.wadii.utils.RequestState


class AdsUseCase(private val adsRepo: AdsRepo) {

    suspend fun ads(
        response: (RequestState<BaseResponse<List<Ads>>>) -> Unit
    ) = adsRepo.ads(response)

}