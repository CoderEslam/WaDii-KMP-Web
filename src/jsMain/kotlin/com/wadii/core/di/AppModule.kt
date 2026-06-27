package com.wadii.core.di

import com.wadii.data.api.ApiService
import com.wadii.data.api.createHttpClient
import com.wadii.data.api.createHttpClientSendFile
import com.wadii.data.repo.AdminRepoImpl
import com.wadii.data.repo.AdsRepoImpl
import com.wadii.data.repo.OfferRepoImpl
import com.wadii.data.repo.ProviderRepoImpl
import com.wadii.data.repo.ServicesRepoImpl
import com.wadii.domain.repo.AdminRepo
import com.wadii.domain.repo.AdsRepo
import com.wadii.domain.repo.OfferRepo
import com.wadii.domain.repo.ProviderRepo
import com.wadii.domain.repo.ServicesRepo
import com.wadii.domain.usecase.AdminDashboardUseCase
import com.wadii.domain.usecase.AdsUseCase
import com.wadii.domain.usecase.OfferUseCase
import com.wadii.domain.usecase.ProviderUseCase
import com.wadii.screens.home.HomeViewModel
import com.wadii.viewmodel.ServicesUseCase
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.dsl.module

val appModule = module {

//    single<SettingsManager> {
//        SettingsManager(getSettings())
//    }
    single<HttpClient> {
        createHttpClient()
    }
    single<ApiService> {
        ApiService(get(), createHttpClientSendFile())
    }
    single<OfferRepo> {
        OfferRepoImpl(get())
    }
    single<OfferUseCase> {
        OfferUseCase(get())
    }
    single<AdsRepo> {
        AdsRepoImpl(get())
    }
    single<AdsUseCase> {
        AdsUseCase(get())
    }
    single<ServicesRepo> {
        ServicesRepoImpl(get())
    }
    single<ServicesUseCase> {
        ServicesUseCase(get())
    }
    single<ProviderRepo> {
        ProviderRepoImpl(get())
    }
    single<ProviderUseCase> {
        ProviderUseCase(get())
    }
    single {
        HomeViewModel(get(), get(), get(), get())
    }
    single<AdminRepo> {
        AdminRepoImpl(get())
    }
    single<AdminDashboardUseCase> {
        AdminDashboardUseCase(get())
    }


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