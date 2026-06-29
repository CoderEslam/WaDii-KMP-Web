package com.wadii.domain.repo

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.utils.RequestState


interface OfferRepo {

    suspend fun insertOffer(
        request: SavedOfferRequest,
        response: (RequestState<BaseResponse<SavedOffer>>) -> Unit
    )

    suspend fun getOffersByServiceId(
        id: Int,
        response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit
    )

    suspend fun offersList(
        response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit
    )

    suspend fun removeSavedOffer(
        offerId: Long,
        response: (RequestState<BaseResponse<String>>) -> Unit
    )

    suspend fun getMySavedOffers(
        response: (RequestState<BaseResponse<List<SavedOffer>>>) -> Unit
    )

    suspend fun createOffer(
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    )

    suspend fun updateOffer(
        offerId: Int,
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    )

    suspend fun deleteOffer(
        offerId: Int,
        response: (RequestState<BaseResponse<String>>) -> Unit
    )
}
