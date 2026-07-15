package com.teacheronline.pages.shared.call

sealed class CallEvent {
    object ToggleMic : CallEvent()
    object ToggleCamera : CallEvent()
    object EndCall : CallEvent()
}
