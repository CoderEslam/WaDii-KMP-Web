package com.wadii.domain.usecase

import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.repo.OfferRepo
import com.wadii.utils.RequestState


class OfferUseCase(private val offerRepo: OfferRepo) {

    suspend fun insertOffer(
        request: SavedOfferRequest,
        response: (RequestState<BaseResponse<SavedOffer>>) -> Unit
    ) = offerRepo.insertOffer(request, response)

    suspend fun getOffersByServiceId(
        id: Int,
        response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit
    ) = offerRepo.getOffersByServiceId(id, response)

    suspend fun removeSavedOffer(
        offerId: Long,
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) = offerRepo.removeSavedOffer(offerId, response)

    suspend fun getMySavedOffers(
        response: (RequestState<BaseResponse<List<SavedOffer>>>) -> Unit
    ) = offerRepo.getMySavedOffers(response)

    suspend fun createOffer(
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) = offerRepo.createOffer(request, response)

    suspend fun updateOffer(
        offerId: Int,
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) = offerRepo.updateOffer(offerId, request, response)

    suspend fun deleteOffer(
        offerId: Int,
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) = offerRepo.deleteOffer(offerId, response)
}
