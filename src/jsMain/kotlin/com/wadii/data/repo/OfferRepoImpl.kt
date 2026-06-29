package com.wadii.data.repo

import com.wadii.data.api.ApiService
import com.wadii.domain.model.BaseResponse
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.model.offers.saved.SavedOffer
import com.wadii.domain.repo.OfferRepo
import com.wadii.utils.RequestState


class OfferRepoImpl(private val apiService: ApiService) : OfferRepo {

    override suspend fun insertOffer(
        request: SavedOfferRequest,
        response: (RequestState<BaseResponse<SavedOffer>>) -> Unit
    ) = apiService.insertOffer(request, response)

    override suspend fun getOffersByServiceId(
        id: Int,
        response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit
    ) = apiService.getOffersByServiceId(id, response)

    override suspend fun offersList(response: (RequestState<BaseResponse<List<OfferResponse>>>) -> Unit) =
        apiService.getOffersList(response)

    override suspend fun removeSavedOffer(
        offerId: Long,
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) = apiService.removeSavedOffer(offerId, response)

    override suspend fun getMySavedOffers(
        response: (RequestState<BaseResponse<List<SavedOffer>>>) -> Unit
    ) = apiService.getMySavedOffers(response)

    override suspend fun createOffer(
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) = apiService.createOffer(request, response)

    override suspend fun updateOffer(
        offerId: Int,
        request: NewOfferRequest,
        response: (RequestState<BaseResponse<OfferResponse>>) -> Unit
    ) = apiService.updateOffer(offerId, request, response)

    override suspend fun deleteOffer(
        offerId: Int,
        response: (RequestState<BaseResponse<String>>) -> Unit
    ) = apiService.deleteOffer(offerId, response)
}
