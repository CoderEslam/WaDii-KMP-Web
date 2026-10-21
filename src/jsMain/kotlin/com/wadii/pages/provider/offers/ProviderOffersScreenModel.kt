package com.wadii.pages.provider.offers

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.domain.model.offers.NewOfferRequest
import com.wadii.domain.model.offers.OfferResponse
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.domain.model.BaseResponse
import com.wadii.state.AppState
import com.wadii.utils.RequestState
import com.wadii.utils.RequestValidators
import com.wadii.utils.firstMessage
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
        val providerId = AppState.user?.provider?.id ?: 0L
        val errors = RequestValidators.offer(title, description, endDate, providerId, offer?.id)
        if (errors.isNotEmpty()) {
            AppState.toast(errors.firstMessage(), true)
            return@launch
        }
        val request = NewOfferRequest(
            id = offer?.id ?: 0L,
            title = title.trim(),
            description = description.trim(),
            endDate = endDate,
            serviceIds = services,
            providerId = providerId
        )
        // Editing must hit /offers/update; sending an existing offer to insert created a duplicate deal.
        val callback: (RequestState<BaseResponse<OfferResponse>>) -> Unit = { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = {
                    offersList()
                    AppState.toast(if (offer != null) "Offer updated!" else "Offer created!")
                }, onError = { error, _ ->
                    updateState { it.copy(isLoading = false) }
                    AppState.toast(error.ifBlank { "Couldn't save the offer. Please try again." }, true)
                }
            )
        }
        if (offer != null) offerUseCase.updateOffer(offer.id.toInt(), request, callback)
        else offerUseCase.createOffer(request, callback)
    }
}
