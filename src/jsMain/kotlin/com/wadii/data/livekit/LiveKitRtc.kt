package com.wadii.data.livekit

import org.w3c.dom.HTMLMediaElement
import kotlin.js.Promise

@JsModule("livekit-client")
@JsNonModule
external object LiveKitClient {
    class Room {
        fun connect(url: String, token: String, options: dynamic = definedExternally): Promise<Unit>
        fun disconnect(stopTracks: Boolean = definedExternally): Promise<Unit>
        val localParticipant: LocalParticipant
        fun on(event: String, callback: (dynamic, dynamic, dynamic) -> Unit): Room
    }

    class LocalParticipant {
        fun setMicrophoneEnabled(enabled: Boolean): Promise<dynamic>
        fun setCameraEnabled(enabled: Boolean): Promise<dynamic>
    }

    class RemoteParticipant {
        val identity: String
    }

    class Track {
        val kind: String
        fun attach(): HTMLMediaElement
        fun attach(element: HTMLMediaElement): HTMLMediaElement
        fun detach()
    }
}
