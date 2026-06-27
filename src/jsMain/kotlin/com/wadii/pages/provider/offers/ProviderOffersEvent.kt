package com.wadii.pages.provider.offers

import com.wadii.domain.model.offers.OfferResponse


sealed class ProviderOffersEvent {
    object Load : ProviderOffersEvent()
    data class ShowModal(val offer: OfferResponse?) : ProviderOffersEvent()
    object CloseModal : ProviderOffersEvent()
    data class Delete(val offerId: Long) : ProviderOffersEvent()
    data class Save(
        val offer: OfferResponse?,
        val title: String,
        val description: String,
        val endDate: String,
        val selectedServices: Set<Int>
    ) : ProviderOffersEvent()
}
