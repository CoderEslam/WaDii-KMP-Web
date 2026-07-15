package com.teacheronline.data.livekit

import kotlinx.coroutines.await

class LiveKitCallClient {

    private var room: LiveKitClient.Room? = null

    var onTrackSubscribed: ((LiveKitClient.Track, LiveKitClient.RemoteParticipant) -> Unit)? = null
    var onParticipantDisconnected: ((LiveKitClient.RemoteParticipant) -> Unit)? = null

    suspend fun join(url: String, token: String, withVideo: Boolean): LiveKitClient.Track? {

        // Already connected
        room?.let { return null }

        return runCatching {

            val r = LiveKitClient.Room()
            room = r

            registerEvents(r)

            r.connect(url, token).await()
            r.localParticipant.setMicrophoneEnabled(true).await()

            if (withVideo) {
                val publication = r.localParticipant.setCameraEnabled(true).await()
                publication?.track?.unsafeCast<LiveKitClient.Track>()
            } else null

        }.getOrElse {
            leave()
            throw it
        }
    }

    private fun registerEvents(room: LiveKitClient.Room) {

        room.on("trackSubscribed") { track, _, participant ->
            onTrackSubscribed?.invoke(
                track.unsafeCast<LiveKitClient.Track>(),
                participant.unsafeCast<LiveKitClient.RemoteParticipant>()
            )
        }

        room.on("participantDisconnected") { participant, _, _ ->
            onParticipantDisconnected?.invoke(
                participant.unsafeCast<LiveKitClient.RemoteParticipant>()
            )
        }
    }

    suspend fun setMicEnabled(enabled: Boolean) {
        room?.localParticipant?.setMicrophoneEnabled(enabled)?.await()
    }

    suspend fun setCameraEnabled(enabled: Boolean) {
        room?.localParticipant?.setCameraEnabled(enabled)?.await()
    }

    suspend fun leave() {
        runCatching { room?.disconnect(true)?.await() }
        room = null
    }
}
