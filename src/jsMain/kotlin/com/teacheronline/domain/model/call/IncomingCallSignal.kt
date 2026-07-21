package com.teacheronline.domain.model.call

import com.teacheronline.domain.model.chat.SocketEvent

data class IncomingCallSignal(
    val event: SocketEvent,
    val signal: CallSignal
)
