package com.wadii.pages.provider.offers

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.state.AppState
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderOffersViewModel(
    private val offerUseCase: OfferUseCase,
    private val servicesUseCase: ServicesUseCase
) : BaseViewModel<ProviderOffersState, ProviderOffersEvent>() {

    override val initialState: ProviderOffersState get() = ProviderOffersState()

    override val state: StateFlow<ProviderOffersState> = _state
        .onStart {
            offersList()
            getServiceList()
        }
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: ProviderOffersEvent) {
        when (event) {
            is ProviderOffersEvent.ShowModal -> updateState {
                it.copy(
                    showModal = true,
                    editOffer = event.offer
                )
            }

            ProviderOffersEvent.CloseModal -> updateState {
                it.copy(
                    showModal = false,
                    editOffer = null
                )
            }

            is ProviderOffersEvent.Delete -> delete(event.offerId)
            is ProviderOffersEvent.Save -> save(
                event.offer,
                event.title,
                event.description,
                event.endDate,
                event.selectedServices
            )
        }
    }


    private fun offersList() = screenModelScope.launch {
        offerUseCase.offersList { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            offers = data.data,
                            isLoading = false
                        )
                    }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false) }
                }
            )
        }
    }

    private fun getServiceList() = screenModelScope.launch {
        servicesUseCase.getServiceList { r ->
            r.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            services = data.data ?: emptyList()
                        )
                    }
                },
                onError = { _, _ -> }
            )
        }
    }

    private fun delete(id: Long) = screenModelScope.launch {
        offerUseCase.deleteOffer(id.toInt()) { r ->
            r.handelState(
                onLoading = {},
                onSuccess = { _ ->
                    AppState.toast("Deleted");
                    offersList()
                },
                onError = { _, _ -> AppState.toast("Failed to delete", true) }
            )
        }
    }

    private fun save(
        offer: OfferResponse?,
        title: String,
        description: String,
        endDate: String,
        services: Set<Long>
    ) = screenModelScope.launch {
        offerUseCase.createOffer(
            request = NewOfferRequest(
                id = offer?.id ?: 0L,
                title = title,
                description = description,
                endDate = endDate,
                serviceIds = services,
                providerId = AppState.user?.provider?.id ?: 0L
            )
        ) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = {
                    offersList()
                    AppState.toast(if (offer != null) "Offer updated!" else "Offer created!")
                }, onError = { error, code ->
                    AppState.toast("Failed to save offer", true)
                }
            )
        }
    }
}
