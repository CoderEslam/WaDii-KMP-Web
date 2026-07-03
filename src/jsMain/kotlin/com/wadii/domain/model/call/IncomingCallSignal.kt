package com.wadii.domain.model.call

import com.wadii.domain.model.chat.SocketEvent

data class IncomingCallSignal(
    val event: SocketEvent,
    val signal: CallSignal
)
