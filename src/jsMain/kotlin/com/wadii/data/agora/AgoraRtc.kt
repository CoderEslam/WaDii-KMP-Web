package com.wadii.data.agora

import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@JsModule("agora-rtc-sdk-ng")
@JsNonModule
external object AgoraRTC {
    fun createClient(config: AgoraClientConfig): IAgoraRTCClient
    fun createMicrophoneAudioTrack(): Promise<IMicrophoneAudioTrack>
    fun createCameraVideoTrack(): Promise<ICameraVideoTrack>
}

external interface AgoraClientConfig {
    var mode: String
    var codec: String
}

fun rtcClientConfig(): AgoraClientConfig =
    js("({})").unsafeCast<AgoraClientConfig>().apply {
        mode = "rtc"
        codec = "vp8"
    }

external interface ILocalTrack {
    fun setEnabled(enabled: Boolean): Promise<Unit>
    fun close()
}

external interface IMicrophoneAudioTrack : ILocalTrack

external interface ICameraVideoTrack : ILocalTrack {
    fun play(element: HTMLElement)
}

external interface IRemoteVideoTrack {
    fun play(element: HTMLElement)
}

external interface IRemoteAudioTrack {
    fun play()
}

external interface IAgoraRTCRemoteUser {
    val uid: dynamic
}

external interface IAgoraRTCClient {
    fun join(appId: String, channel: String, token: String?, uid: dynamic): Promise<dynamic>
    fun publish(tracks: Array<ILocalTrack>): Promise<Unit>
    fun subscribe(user: IAgoraRTCRemoteUser, mediaType: String): Promise<dynamic>
    fun leave(): Promise<Unit>
    fun on(event: String, listener: (arg1: dynamic, arg2: dynamic) -> Unit)
}
