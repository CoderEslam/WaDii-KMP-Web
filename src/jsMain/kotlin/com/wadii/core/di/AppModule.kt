package com.wadii.core.di

import com.wadii.data.api.ApiService
import com.wadii.data.api.createHttpClient
import com.wadii.data.api.createHttpClientSendFile
import com.wadii.data.repo.AdminRepoImpl
import com.wadii.domain.repo.AdminRepo
import com.wadii.domain.usecase.AdminDashboardUseCase
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