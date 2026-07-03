package com.wadii.core.di

import com.wadii.core.call.CallSignalingController
import com.wadii.data.api.ApiService
import com.wadii.data.api.createHttpClient
import com.wadii.data.api.createHttpClientSendFile
import com.wadii.data.repo.AdminRepoImpl
import com.wadii.data.repo.AdsRepoImpl
import com.wadii.data.repo.AgoraRepoImpl
import com.wadii.data.repo.AuthRepoImpl
import com.wadii.data.repo.CountryRepoImpl
import com.wadii.data.repo.MessagesRepoImpl
import com.wadii.data.repo.NotificationRepoImpl
import com.wadii.data.repo.OfferRepoImpl
import com.wadii.data.repo.OrderRepoImpl
import com.wadii.data.repo.ProviderRepoImpl
import com.wadii.data.repo.ResponseRepoImpl
import com.wadii.data.repo.SearchRepoImpl
import com.wadii.data.repo.ServicesRepoImpl
import com.wadii.data.repo.UserRepoImpl
import com.wadii.data.websocket.CallSignalingService
import com.wadii.domain.repo.AdminRepo
import com.wadii.domain.repo.AdsRepo
import com.wadii.domain.repo.AgoraRepo
import com.wadii.domain.repo.AuthRepo
import com.wadii.domain.repo.CountryRepo
import com.wadii.domain.repo.MessagesRepo
import com.wadii.domain.repo.NotificationRepo
import com.wadii.domain.repo.OfferRepo
import com.wadii.domain.repo.OrderRepo
import com.wadii.domain.repo.ProviderRepo
import com.wadii.domain.repo.ResponseRepo
import com.wadii.domain.repo.SearchRepo
import com.wadii.domain.repo.ServicesRepo
import com.wadii.domain.repo.UserRepo
import com.wadii.domain.usecase.AdminDashboardUseCase
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.domain.usecase.AgoraUseCase
import com.wadii.domain.usecase.AuthUseCase
import com.wadii.domain.usecase.CountryUseCase
import com.wadii.domain.usecase.MessageUseCase
import com.wadii.domain.usecase.NotificationUseCase
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.domain.usecase.OrderUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.domain.usecase.ResponseUseCase
import com.wadii.domain.usecase.SearchUseCase
import com.wadii.domain.usecase.UserUseCase
import com.wadii.pages.shared.call.CallViewModel
import com.wadii.pages.admin.ads.AdsScreenModel
import com.wadii.pages.admin.dashboard.AdminDashboardScreenModel
import com.wadii.pages.admin.provider.ProviderRequestsViewModel
import com.wadii.pages.admin.service.ServicesViewModel
import com.wadii.pages.provider.dashboard.ProviderDashboardViewModel
import com.wadii.pages.provider.offers.ProviderOffersViewModel
import com.wadii.pages.provider.orders.ProviderOrdersViewModel
import com.wadii.pages.provider.respond.RespondToOrderViewModel
import com.wadii.data.api.clientWebSocket
import com.wadii.data.websocket.ChatWebSocketService
import com.wadii.pages.shared.chat.ChatViewModel
import com.wadii.pages.shared.notifications.NotificationsViewModel
import com.wadii.pages.shared.profile.EditProfileViewModel
import com.wadii.pages.shared.profile.ProfileViewModel
import com.wadii.screens.auth.login.LoginViewModel
import com.wadii.screens.auth.register.RegisterViewModel
import com.wadii.screens.home.HomeViewModel
import com.wadii.screens.orders.detail.OrderDetailViewModel
import com.wadii.screens.orders.list.OrdersViewModel
import com.wadii.screens.orders.new.NewOrderViewModel
import com.wadii.screens.providerDetail.ProviderDetailViewModel
import com.wadii.screens.savedOffers.SavedOffersViewModel
import com.wadii.screens.search.SearchViewModel
import com.wadii.viewmodel.ServicesUseCase
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {

    single<HttpClient> { createHttpClient() }
    single<ApiService> { ApiService(get(), createHttpClientSendFile()) }
    single(named("wsClient")) { clientWebSocket() }
    single { ChatWebSocketService(get(named("wsClient"))) }
    single { CallSignalingService(get(named("wsClient"))) }
    single { CallSignalingController(get()) }

    // Repos
    single<AgoraRepo> { AgoraRepoImpl(get()) }
    single<AuthRepo> { AuthRepoImpl(get()) }
    single<OrderRepo> { OrderRepoImpl(get()) }
    single<OfferRepo> { OfferRepoImpl(get()) }
    single<AdsRepo> { AdsRepoImpl(get()) }
    single<ServicesRepo> { ServicesRepoImpl(get()) }
    single<ProviderRepo> { ProviderRepoImpl(get()) }
    single<AdminRepo> { AdminRepoImpl(get()) }
    single<SearchRepo> { SearchRepoImpl(get()) }
    single<ResponseRepo> { ResponseRepoImpl(get()) }
    single<CountryRepo> { CountryRepoImpl(get()) }
    single<MessagesRepo> { MessagesRepoImpl(get()) }
    single<NotificationRepo> { NotificationRepoImpl(get()) }
    single<UserRepo> { UserRepoImpl(get()) }

    // UseCases
    single { AgoraUseCase(get()) }
    single { AuthUseCase(get()) }
    single { OrderUseCase(get()) }
    single { OfferUseCase(get()) }
    single { AdsUseCase(get()) }
    single { ServicesUseCase(get()) }
    single { ProviderUseCase(get()) }
    single { AdminDashboardUseCase(get()) }
    single { SearchUseCase(get()) }
    single { ResponseUseCase(get()) }
    single { CountryUseCase(get()) }
    single { MessageUseCase(get()) }
    single { NotificationUseCase(get()) }
    single { UserUseCase(get()) }

    // ViewModels — screens/
    factory { HomeViewModel(get(), get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { OrdersViewModel(get()) }
    factory { (orderId: Int) -> OrderDetailViewModel(orderId, get()) }
    factory { NewOrderViewModel(get(), get()) }
    factory { SearchViewModel(get()) }
    factory { SavedOffersViewModel(get()) }
    factory { (providerId: Int) -> ProviderDetailViewModel(providerId, get()) }
    factory { params ->
        CallViewModel(
            channelName = params.get(),
            remoteUserId = params.get(),
            remoteUserName = params.get(),
            remoteUserImage = params.get(),
            withVideo = params.get(),
            isCaller = params.get(),
            agoraUseCase = get(),
            callSignaling = get()
        )
    }

    // ViewModels — pages/
    factory { ProviderDashboardViewModel(get(), get()) }
    factory { ProviderOrdersViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { EditProfileViewModel(get(), get(), get(), get()) }
    factory { NotificationsViewModel(get()) }
    factory { ProviderRequestsViewModel(get()) }
    factory { ServicesViewModel(get()) }
    factory { ChatViewModel(get(), get()) }
    factory { ProviderOffersViewModel(get(), get()) }
    factory { (orderId: Int) -> RespondToOrderViewModel(orderId, get()) }
    factory { AdminDashboardScreenModel(get(), get(), get()) }
    factory { AdsScreenModel(get()) }
}

fun initializeKoin() {
    try {
        startKoin {
            printLogger(Level.DEBUG)
            modules(appModule)
        }
    } catch (e: KoinApplicationAlreadyStartedException) {
        println(e.message)
    }
}
