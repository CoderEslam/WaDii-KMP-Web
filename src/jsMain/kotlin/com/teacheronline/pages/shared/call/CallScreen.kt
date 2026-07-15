package com.teacheronline.pages.shared.call

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.teacheronline.ui.Avatar
import com.teacheronline.ui.AvatarSize
import com.teacheronline.ui.DangerButton
import com.teacheronline.ui.SecondaryButton
import com.teacheronline.utils.Constants
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.koin.core.parameter.parametersOf
import org.w3c.dom.HTMLDivElement

class CallScreen(
    private val channelName: String,
    private val remoteUserId: Long,
    private val remoteUserName: String,
    private val remoteUserImage: String?,
    private val withVideo: Boolean,
    private val isCaller: Boolean
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<CallViewModel> {
            parametersOf(channelName, remoteUserId, remoteUserName, remoteUserImage, withVideo, isCaller)
        }
        val state by model.state.collectAsState()

        var localContainer by remember { mutableStateOf<HTMLDivElement?>(null) }
        var remoteContainer by remember { mutableStateOf<HTMLDivElement?>(null) }

        val localTrack = state.localVideoTrack
        val remoteTrack = state.remoteVideoTrack

        DisposableEffect(localTrack, localContainer) {
            val container = localContainer
            val element = if (localTrack != null && container != null) {
                localTrack.attach().also { container.appendChild(it) }
            } else null

            onDispose {
                element?.let {
                    localTrack?.detach()
                    container?.removeChild(it)
                }
            }
        }
        DisposableEffect(remoteTrack, remoteContainer) {
            val container = remoteContainer
            val element = if (remoteTrack != null && container != null) {
                remoteTrack.attach().also { container.appendChild(it) }
            } else null

            onDispose {
                element?.let {
                    remoteTrack?.detach()
                    container?.removeChild(it)
                }
            }
        }
        LaunchedEffect(state.status) {
            if (state.status == CallStatus.ENDED) {
                delay(1200)
                navigator.pop()
            }
        }

        Div(attrs = {
            classes("relative", "flex", "flex-col")
            style { property("height", "calc(100vh - 8rem)") }
        }) {
            Div(attrs = {
                classes("flex-1", "bg-surface-secondary", "rounded-neu-base", "overflow-hidden", "relative")
                ref { element ->
                    remoteContainer = element
                    onDispose { remoteContainer = null }
                }
            }) {
                if (!state.remoteHasVideo) {
                    Div(attrs = {
                        classes("absolute", "inset-0", "flex", "items-center", "justify-center")
                    }) {
                        Avatar(
                            imageUrl = state.remoteUserImage,
                            initials = state.remoteUserName.take(1).uppercase(),
                            size = AvatarSize.XXL
                        )
                    }
                }
            }
            if (state.withVideo) {
                Div(attrs = {
                    classes(
                        "absolute", "bottom-24", "right-4", "w-32", "h-44",
                        "bg-surface-secondary", "rounded-neu-base", "overflow-hidden", "border", "border-default"
                    )
                    ref { element ->
                        localContainer = element
                        onDispose { localContainer = null }
                    }
                }) {}
            }
            Div(attrs = { classes("p-4", "flex", "flex-col", "items-center", "gap-3") }) {
                P(attrs = { classes("text-heading", "font-medium") }) {
                    Text(
                        when (state.status) {
                            CallStatus.CALLING -> "Calling ${state.remoteUserName}…"
                            CallStatus.RINGING -> "Ringing…"
                            CallStatus.CONNECTING -> "Connecting…"
                            CallStatus.CONNECTED -> state.remoteUserName
                            CallStatus.ENDED -> state.error.ifBlank { "Call ended" }
                        }
                    )
                }
                Div(attrs = { classes("flex", "gap-3") }) {
                    if (state.status == CallStatus.CONNECTED) {
                        SecondaryButton(if (state.micEnabled) "Mute" else "Unmute") {
                            model.onEvent(CallEvent.ToggleMic)
                        }
                        if (state.withVideo) {
                            SecondaryButton(if (state.cameraEnabled) "Camera Off" else "Camera On") {
                                model.onEvent(CallEvent.ToggleCamera)
                            }
                        }
                    }
                    DangerButton("End Call") { model.onEvent(CallEvent.EndCall) }
                }
            }
        }
    }
}
