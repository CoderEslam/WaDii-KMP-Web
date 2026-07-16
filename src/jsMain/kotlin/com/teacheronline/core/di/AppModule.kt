package com.teacheronline.core.di

import com.teacheronline.core.call.CallSignalingController
import com.teacheronline.data.api.ApiService
import com.teacheronline.data.api.clientWebSocket
import com.teacheronline.data.api.createHttpClient
import com.teacheronline.data.repo.AttendanceRepoImpl
import com.teacheronline.data.repo.AuthRepoImpl
import com.teacheronline.data.repo.ConfigRepoImpl
import com.teacheronline.data.repo.EducationalCenterRepoImpl
import com.teacheronline.data.repo.LanguageRepoImpl
import com.teacheronline.data.repo.LevelRepoImpl
import com.teacheronline.data.repo.LiveKitRepoImpl
import com.teacheronline.data.repo.MessagesRepoImpl
import com.teacheronline.data.repo.ParentRepoImpl
import com.teacheronline.data.repo.PaymentRepoImpl
import com.teacheronline.data.repo.ScheduleRepoImpl
import com.teacheronline.data.repo.SecretaryRepoImpl
import com.teacheronline.data.repo.StudentRepoImpl
import com.teacheronline.data.repo.SubjectRepoImpl
import com.teacheronline.data.repo.TeacherRepoImpl
import com.teacheronline.data.repo.TranslateRepoImpl
import com.teacheronline.data.websocket.CallSignalingService
import com.teacheronline.data.websocket.ChatWebSocketService
import com.teacheronline.domain.repo.AttendanceRepo
import com.teacheronline.domain.repo.AuthRepo
import com.teacheronline.domain.repo.ConfigRepo
import com.teacheronline.domain.repo.EducationalCenterRepo
import com.teacheronline.domain.repo.LanguageRepo
import com.teacheronline.domain.repo.LevelRepo
import com.teacheronline.domain.repo.LiveKitRepo
import com.teacheronline.domain.repo.MessagesRepo
import com.teacheronline.domain.repo.ParentRepo
import com.teacheronline.domain.repo.PaymentRepo
import com.teacheronline.domain.repo.ScheduleRepo
import com.teacheronline.domain.repo.SecretaryRepo
import com.teacheronline.domain.repo.StudentRepo
import com.teacheronline.domain.repo.SubjectRepo
import com.teacheronline.domain.repo.TeacherRepo
import com.teacheronline.domain.repo.TranslateRepo
import com.teacheronline.domain.usecase.AttendanceUseCase
import com.teacheronline.domain.usecase.AuthUseCase
import com.teacheronline.domain.usecase.ConfigUseCase
import com.teacheronline.domain.usecase.EducationalCenterUseCase
import com.teacheronline.domain.usecase.LanguageUseCase
import com.teacheronline.domain.usecase.LevelUseCase
import com.teacheronline.domain.usecase.LiveKitUseCase
import com.teacheronline.domain.usecase.MessageUseCase
import com.teacheronline.domain.usecase.ParentUseCase
import com.teacheronline.domain.usecase.PaymentUseCase
import com.teacheronline.domain.usecase.ScheduleUseCase
import com.teacheronline.domain.usecase.SecretaryUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.domain.usecase.TranslateUseCase
import com.teacheronline.pages.admin.centers.CentersViewModel
import com.teacheronline.pages.admin.config.ConfigViewModel
import com.teacheronline.pages.admin.dashboard.AdminDashboardScreenModel
import com.teacheronline.pages.admin.languages.LanguagesViewModel
import com.teacheronline.pages.admin.levels.LevelsViewModel
import com.teacheronline.pages.admin.payments.PaymentsViewModel
import com.teacheronline.pages.admin.schedule.AdminScheduleViewModel
import com.teacheronline.pages.admin.secretaries.SecretariesViewModel
import com.teacheronline.pages.admin.students.AdminStudentsViewModel
import com.teacheronline.pages.admin.subjects.SubjectsViewModel
import com.teacheronline.pages.admin.teachers.TeachersViewModel
import com.teacheronline.pages.parent.dashboard.ParentDashboardViewModel
import com.teacheronline.pages.secretary.dashboard.SecretaryDashboardViewModel
import com.teacheronline.pages.shared.call.CallViewModel
import com.teacheronline.pages.shared.chat.ChatViewModel
import com.teacheronline.pages.shared.profile.ProfileViewModel
import com.teacheronline.pages.student.dashboard.StudentDashboardViewModel
import com.teacheronline.pages.teacher.attendance.TeacherAttendanceViewModel
import com.teacheronline.pages.teacher.dashboard.TeacherDashboardViewModel
import com.teacheronline.pages.teacher.payments.TeacherPaymentsViewModel
import com.teacheronline.pages.teacher.schedule.TeacherScheduleViewModel
import com.teacheronline.pages.teacher.secretaries.TeacherSecretariesViewModel
import com.teacheronline.pages.teacher.students.TeacherStudentsViewModel
import com.teacheronline.screens.auth.login.LoginViewModel
import com.teacheronline.screens.auth.register.RegisterViewModel
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single<HttpClient> { createHttpClient() }
    single { ApiService(get()) }
    single(named("wsClient")) { clientWebSocket() }
    single { ChatWebSocketService(get(named("wsClient"))) }
    single { CallSignalingService(get(named("wsClient"))) }
    single { CallSignalingController(get()) }

    // Repos
    single<AuthRepo> { AuthRepoImpl(get()) }
    single<TeacherRepo> { TeacherRepoImpl(get()) }
    single<SubjectRepo> { SubjectRepoImpl(get()) }
    single<LanguageRepo> { LanguageRepoImpl(get()) }
    single<EducationalCenterRepo> { EducationalCenterRepoImpl(get()) }
    single<StudentRepo> { StudentRepoImpl(get()) }
    single<ParentRepo> { ParentRepoImpl(get()) }
    single<AttendanceRepo> { AttendanceRepoImpl(get()) }
    single<ConfigRepo> { ConfigRepoImpl(get()) }
    single<LevelRepo> { LevelRepoImpl(get()) }
    single<PaymentRepo> { PaymentRepoImpl(get()) }
    single<ScheduleRepo> { ScheduleRepoImpl(get()) }
    single<SecretaryRepo> { SecretaryRepoImpl(get()) }
    single<TranslateRepo> { TranslateRepoImpl(get()) }
    single<LiveKitRepo> { LiveKitRepoImpl(get()) }
    single<MessagesRepo> { MessagesRepoImpl(get()) }

    // UseCases
    single { AuthUseCase(get()) }
    single { TeacherUseCase(get()) }
    single { SubjectUseCase(get()) }
    single { LanguageUseCase(get()) }
    single { EducationalCenterUseCase(get()) }
    single { StudentUseCase(get()) }
    single { ParentUseCase(get()) }
    single { AttendanceUseCase(get()) }
    single { ConfigUseCase(get()) }
    single { LevelUseCase(get()) }
    single { PaymentUseCase(get()) }
    single { ScheduleUseCase(get()) }
    single { SecretaryUseCase(get()) }
    single { TranslateUseCase(get()) }
    single { LiveKitUseCase(get()) }
    single { MessageUseCase(get()) }

    // ViewModels — auth
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }

    // ViewModels — shared
    factory { ProfileViewModel() }
    factory { ChatViewModel(get(), get()) }
    factory { params ->
        CallViewModel(
            channelName = params.get(),
            remoteUserId = params.get(),
            remoteUserName = params.get(),
            remoteUserImage = params.get(),
            withVideo = params.get(),
            isCaller = params.get(),
            liveKitUseCase = get(),
            callSignaling = get()
        )
    }

    // ViewModels — ADMIN (owner) console
    factory { AdminDashboardScreenModel(get(), get(), get(), get()) }
    factory { CentersViewModel(get()) }
    factory { TeachersViewModel(get(), get()) }
    factory { SubjectsViewModel(get()) }
    factory { LanguagesViewModel(get()) }
    factory { AdminStudentsViewModel(get(), get(), get(), get()) }
    factory { SecretariesViewModel(get(), get()) }
    factory { LevelsViewModel(get()) }
    factory { AdminScheduleViewModel(get(), get(), get()) }
    factory { ConfigViewModel(get()) }
    factory { PaymentsViewModel(get(), get(), get()) }

    // ViewModels — TEACHER portal
    factory { TeacherDashboardViewModel(get()) }
    factory { TeacherStudentsViewModel(get(), get()) }
    factory { TeacherAttendanceViewModel(get(), get()) }
    factory { TeacherScheduleViewModel(get(), get()) }
    factory { TeacherPaymentsViewModel(get(), get()) }
    factory { TeacherSecretariesViewModel(get(), get()) }

    // ViewModels — STUDENT portal
    factory { StudentDashboardViewModel(get(), get(), get()) }

    // ViewModels — PARENTS portal
    factory { ParentDashboardViewModel(get(), get()) }

    // ViewModels — SECRETARY portal
    factory { SecretaryDashboardViewModel(get(), get(), get(), get(), get(), get()) }
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
