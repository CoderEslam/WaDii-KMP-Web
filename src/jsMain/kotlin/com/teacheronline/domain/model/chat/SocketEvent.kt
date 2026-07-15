package com.teacheronline.domain.model.chat

enum class SocketEvent {
    MESSAGE,
    PRESENCE,
    CALL_INVITE,
    CALL_ACCEPT,
    CALL_REJECT,
    CALL_END
}