package com.wadii.screens.home

import com.wadii.components.MessageType
import com.wadii.domain.model.ads.Ads
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.model.provider.ProviderModel
import com.wadii.domain.model.service.Service


data class HomeState(
    val offers: List<OfferResponse> = emptyList(),
    val ads: List<Ads> = emptyList(),
    val allProviders: List<ProviderModel> = emptyList(),
    val filteredProviders: List<ProviderModel> = emptyList(),
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val messageType: MessageType = MessageType.SUCCESS,
    val message : String = "",
    val selectedService: Service = Service(),
    val filterLoading: Boolean = false
) {
//    val visibleOffers: List<OfferResponse>
//        get() = if (selectedServiceId == null) offers
////        else offers.filter { o -> o.services?.any { it.id == selectedServiceId } == true }
}
