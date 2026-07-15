package com.teacheronline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.teacheronline.core.call.CallSignalingController
import com.teacheronline.core.di.initializeKoin
import com.teacheronline.data.firebase.FcmService
import com.teacheronline.domain.model.auth.Role
import com.teacheronline.domain.model.auth.login.User
import com.teacheronline.pages.admin.dashboard.AdminDashboardScreen
import com.teacheronline.pages.parent.dashboard.ParentDashboardScreen
import com.teacheronline.pages.secretary.dashboard.SecretaryDashboardScreen
import com.teacheronline.pages.shared.call.CallDeepLink
import com.teacheronline.pages.shared.call.CallScreen
import com.teacheronline.pages.shared.call.IncomingCallOverlay
import com.teacheronline.pages.shared.call.parseCallDeepLink
import com.teacheronline.pages.student.dashboard.StudentDashboardScreen
import com.teacheronline.pages.teacher.dashboard.TeacherDashboardScreen
import com.teacheronline.screens.auth.login.LoginScreen
import com.teacheronline.state.AppState
import com.teacheronline.ui.Layout
import com.teacheronline.ui.Toast
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.awaitCancellation
import org.jetbrains.compose.web.renderComposable
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

// Set only when the app is booted via a `#call?...` deep link (e.g. from a mobile WebView
// shell) so App() can jump straight into CallScreen instead of Login/Home. See CallDeepLink.kt.
private var bootCallDeepLink: CallDeepLink? = null

fun main() {
    initializeKoin()
    window.addEventListener("unhandledrejection", { event ->
        if (event.asDynamic().reason?.name == "AbortError") event.preventDefault()
    })
    parseCallDeepLink(window.location.hash)?.let { link ->
        bootCallDeepLink = link
        AppState.token = link.token
        AppState.user = User(id = link.myUserId, firstName = link.myUserName)
    }
    renderComposable(rootElementId = "root") {
       KoinContext {
            App()
        }
    }
}

@Composable
fun App() {
    val darkMode = AppState.darkMode
    SideEffect {
        val cl = document.documentElement?.classList ?: return@SideEffect
        if (darkMode) cl.add("dark") else cl.remove("dark")
    }

    val user = AppState.user
    val token = AppState.token
    val callController = koinInject<CallSignalingController>()
    LaunchedEffect(user?.id, token) {
        if (user != null && token != null) callController.start(user.id, token) else callController.stop()
    }

    LaunchedEffect(user?.id) {
        if (user == null) return@LaunchedEffect
        val unsubscribe = FcmService.observeForegroundMessages { title, body ->
            AppState.toast(if (body.isNotBlank()) "$title: $body" else title)
        }
        try {
            awaitCancellation()
        } finally {
            unsubscribe()
        }
    }

    if (user == null) {
        Navigator(LoginScreen()) { CurrentScreen() }
        Toast()
        return
    }

    bootCallDeepLink?.let { link ->
        // Bare Navigator, no Layout chrome — this boot mode is meant to fill a mobile WebView.
        Navigator(
            CallScreen(
                channelName = link.channelName,
                remoteUserId = link.remoteUserId,
                remoteUserName = link.remoteUserName,
                remoteUserImage = link.remoteUserImage,
                withVideo = link.withVideo,
                isCaller = link.isCaller
            )
        ) { CurrentScreen() }
        Toast()
        return
    }

    val initialScreen = when (user.role) {
        Role.ADMIN -> AdminDashboardScreen()
        Role.TEACHER -> TeacherDashboardScreen()
        Role.STUDENT -> StudentDashboardScreen()
        Role.PARENTS -> ParentDashboardScreen()
        Role.SECRETARY -> SecretaryDashboardScreen()
    }

    Navigator(initialScreen) {
        Layout { CurrentScreen() }
        IncomingCallOverlay()
    }
    Toast()
}
