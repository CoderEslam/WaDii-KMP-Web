package com.wadii.pages.shared.call

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wadii.core.call.CallSignalingController
import com.wadii.domain.model.chat.SocketEvent
import com.wadii.state.AppState
import com.wadii.ui.Avatar
import com.wadii.ui.AvatarSize
import com.wadii.ui.DangerButton
import com.wadii.ui.SuccessButton
import com.wadii.utils.Constants
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.koin.compose.koinInject

@Composable
fun IncomingCallOverlay() {
    val incoming = AppState.incomingCall ?: return
    val navigator = LocalNavigator.currentOrThrow
    val controller = koinInject<CallSignalingController>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(incoming.channelName) {
        controller.events.collect { ev ->
            if (ev.signal.channelName == incoming.channelName && ev.event == SocketEvent.CALL_END) {
                AppState.incomingCall = null
            }
        }
    }

    Div(attrs = {
        classes("fixed", "inset-0", "z-50", "flex", "items-center", "justify-center")
        style { property("background-color", "rgba(0, 0, 0, 0.6)") }
    }) {
        Div(attrs = { classes("bg-surface", "rounded-neu-base", "shadow-neu-lg", "p-6", "text-center") }) {
            Avatar(
                imageUrl = incoming.fromUserImage?.let { "${Constants.BASE_URL_USER_IMAGES}/$it" },
                initials = incoming.fromUserName.take(1).uppercase(),
                size = AvatarSize.XXL
            )
            P(attrs = { classes("font-semibold", "text-heading", "mt-3") }) { Text(incoming.fromUserName) }
            P(attrs = { classes("text-body-subtle", "text-sm") }) {
                Text(if (incoming.callType == "VIDEO") "Incoming video call" else "Incoming voice call")
            }
            Div(attrs = { classes("flex", "gap-4", "justify-center", "mt-4") }) {
                DangerButton("Decline") {
                    val myId = AppState.user?.id ?: 0
                    scope.launch {
                        controller.send(
                            SocketEvent.CALL_REJECT,
                            incoming.copy(fromUserId = myId, toUserId = incoming.fromUserId)
                        )
                    }
                    AppState.incomingCall = null
                }
                SuccessButton("Accept") {
                    val myId = AppState.user?.id ?: 0
                    scope.launch {
                        controller.send(
                            SocketEvent.CALL_ACCEPT,
                            incoming.copy(fromUserId = myId, toUserId = incoming.fromUserId)
                        )
                    }
                    AppState.incomingCall = null
                    navigator.push(
                        CallScreen(
                            channelName = incoming.channelName,
                            remoteUserId = incoming.fromUserId,
                            remoteUserName = incoming.fromUserName,
                            remoteUserImage = incoming.fromUserImage,
                            withVideo = incoming.callType == "VIDEO",
                            isCaller = false
                        )
                    )
                }
            }
        }
    }
}
