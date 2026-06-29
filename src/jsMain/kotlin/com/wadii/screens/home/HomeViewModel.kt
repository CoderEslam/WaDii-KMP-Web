package com.wadii.screens.home

import cafe.adriel.voyager.core.model.screenModelScope
import com.wadii.BaseViewModel
import com.wadii.components.MessageType
import com.wadii.core.isNotNullOrEmptyString
import com.wadii.domain.model.offers.SavedOfferRequest
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.viewmodel.ServicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val adsUseCase: AdsUseCase,
    private val servicesUseCase: ServicesUseCase,
    private val providerUseCase: ProviderUseCase,
    private val offerUseCase: OfferUseCase
) : BaseViewModel<HomeState, HomeEvent>() {


    override val initialState: HomeState
        get() = HomeState()

    override val state: StateFlow<HomeState> = _state
        .onStart {
            ads()
            providers()
            service()
            offers()
        }.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialState
        )

    override fun onEvent(event: HomeEvent) {
        when (event) {

            is HomeEvent.SelectService -> {
                updateState { it.copy(selectedService = event.service) }
                if (event.service.toString().isNotNullOrEmptyString()) {
                    getOffersByServiceId(event.service.id)
                    providersByServiceId(event.service.id)
                } else {
                    offers()
                    providers()
                }
            }

            is HomeEvent.ClearMessage -> {
                updateState { it.copy(message = "") }
            }

            is HomeEvent.ToggleSaveOffer -> {
                val found = _state.value.offers.findWithIndex { it.id == event.offer.id }
                found?.let { (index, offer) ->
                    val isSaved = offer.saved
                    updateState {
                        it.copy(
                            offers = _state.value.offers.toMutableList().apply {
                                set(index, offer.copy(saved = !isSaved))
                            }
                        )
                    }
                    if (isSaved) {
                        removeSavedOffer(event.offer.id)
                    } else {
                        insertOffer(event.offer.id)
                    }
                }
            }
        }
    }

    private fun insertOffer(offerId: Long) = screenModelScope.launch {
        offerUseCase.insertOffer(SavedOfferRequest(offerId = offerId)) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            message = data.message,
                            messageType = MessageType.SUCCESS
                        )
                    }
                },
                onError = { error, _ ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            message = error,
                            messageType = MessageType.ERROR
                        )
                    }
                }
            )
        }
    }

    private fun removeSavedOffer(offerId: Long) = screenModelScope.launch {
        offerUseCase.removeSavedOffer(offerId) { response ->
            response.handelState(
                onLoading = {
                    _state.update { it.copy(isLoading = true) }
                },
                onSuccess = { _ ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = "Offer removed",
                            messageType = MessageType.SUCCESS
                        )
                    }
                },
                onError = { error, _ ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = error,
                            messageType = MessageType.ERROR
                        )
                    }
                }
            )
        }
    }

    private fun ads() = screenModelScope.launch {
        adsUseCase.ads { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(ads = data.data, isLoading = false)
                    }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false) }
                }
            )
        }
    }

    private fun providers() = screenModelScope.launch {
        providerUseCase.providersList { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            allProviders = data.data,
                            filteredProviders = data.data,
                            isLoading = false
                        )
                    }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false) }
                }
            )
        }
    }

    private fun service() = screenModelScope.launch {
        servicesUseCase.getServiceList { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            services = data.data,
                            isLoading = false
                        )
                    }
                }, onError = { error, code ->
                    updateState { it.copy(isLoading = false) }
                }
            )
        }
    }

    private fun offers() = screenModelScope.launch {
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

    fun providersByServiceId(id: Long) = screenModelScope.launch {
        providerUseCase.providersListByServiceId(id = id) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                }, onSuccess = { data ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            allProviders = data.data,
                            filteredProviders = data.data,
                            message = data.message,
                            messageType = MessageType.SUCCESS
                        )
                    }
                }, onError = { error, code ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            message = error,
                            messageType = MessageType.ERROR
                        )
                    }
                }
            )
        }
    }

    private fun getOffersByServiceId(id: Long) = screenModelScope.launch {
        offerUseCase.getOffersByServiceId(id = id) { response ->
            response.handelState(
                onLoading = {
                    updateState { it.copy(isLoading = true) }
                },
                onSuccess = { data ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            offers = data.data,
                            message = data.message,
                            messageType = MessageType.SUCCESS
                        )
                    }
                },
                onError = { error, _ ->
                    updateState {
                        it.copy(isLoading = false, message = error, messageType = MessageType.ERROR)
                    }
                }
            )
        }
    }

}


private inline fun <T> Iterable<T>.findWithIndex(
    predicate: (T) -> Boolean
): Pair<Int, T>? {
    for ((index, element) in this.withIndex()) {
        if (predicate(element)) return index to element
    }
    return null
}