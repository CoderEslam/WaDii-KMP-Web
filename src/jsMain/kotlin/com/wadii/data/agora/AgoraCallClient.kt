package com.wadii.data.agora

import kotlinx.coroutines.await

//class AgoraCallClient {
//    private var client: IAgoraRTCClient? = null
//    private var micTrack: IMicrophoneAudioTrack? = null
//    private var camTrack: ICameraVideoTrack? = null
//
//    var onUserPublished: ((IAgoraRTCRemoteUser, String) -> Unit)? = null
//    var onUserLeft: ((IAgoraRTCRemoteUser) -> Unit)? = null
//
//    suspend fun join(
//        appId: String,
//        channel: String,
//        token: String?,
//        uid: Int,
//        withVideo: Boolean
//    ): ICameraVideoTrack? {
//        val c = AgoraRTC.createClient(rtcClientConfig())
//        client = c
//        c.on("user-published") { user, mediaType ->
//            onUserPublished?.invoke(user.unsafeCast<IAgoraRTCRemoteUser>(), mediaType.toString())
//        }
//        c.on("user-left") { user, _ ->
//            onUserLeft?.invoke(user.unsafeCast<IAgoraRTCRemoteUser>())
//        }
//
//        c.join(appId, channel, token, uid).await()
//
//        val mic = AgoraRTC.createMicrophoneAudioTrack().await()
//        micTrack = mic
//        val tracks = mutableListOf<ILocalTrack>(mic)
//        var cam: ICameraVideoTrack? = null
//        if (withVideo) {
//            cam = AgoraRTC.createCameraVideoTrack().await()
//            camTrack = cam
//            tracks += cam
//        }
//        c.publish(tracks.toTypedArray()).await()
//        return cam
//    }
//
//    suspend fun subscribe(user: IAgoraRTCRemoteUser, mediaType: String): dynamic =
//        client?.subscribe(user, mediaType)?.await()
//
//    suspend fun setMicEnabled(enabled: Boolean) {
//        micTrack?.setEnabled(enabled)?.await()
//    }
//
//    suspend fun setCameraEnabled(enabled: Boolean) {
//        camTrack?.setEnabled(enabled)?.await()
//    }
//
//    suspend fun leave() {
//        runCatching { micTrack?.close() }
//        runCatching { camTrack?.close() }
//        runCatching { client?.leave()?.await() }
//        client = null
//        micTrack = null
//        camTrack = null
//    }
//}

class AgoraCallClient {

    private var client: IAgoraRTCClient? = null
    private var microphoneTrack: IMicrophoneAudioTrack? = null
    private var cameraTrack: ICameraVideoTrack? = null

    var onUserPublished: ((IAgoraRTCRemoteUser, String) -> Unit)? = null
    var onUserLeft: ((IAgoraRTCRemoteUser) -> Unit)? = null

    suspend fun join(
        appId: String,
        channel: String,
        token: String?,
        uid: Long,
        withVideo: Boolean
    ): ICameraVideoTrack? {

        // Already connected
        client?.let { return cameraTrack }

        return runCatching {

            val rtcClient = AgoraRTC.createClient(rtcClientConfig())
            client = rtcClient

            registerEvents(rtcClient)

            rtcClient.join(
                appId,
                channel,
                token,
                uid
            ).await()

            microphoneTrack = AgoraRTC.createMicrophoneAudioTrack().await()

            if (withVideo) {
                cameraTrack = AgoraRTC.createCameraVideoTrack().await()
            }

            val tracks = buildList<ILocalTrack> {
                microphoneTrack?.let(::add)
                cameraTrack?.let(::add)
            }

            rtcClient.publish(tracks.toTypedArray()).await()

            cameraTrack

        }.getOrElse {
            leave()
            throw it
        }
    }

    private fun registerEvents(client: IAgoraRTCClient) {

        client.on("user-published") { user, mediaType ->
            onUserPublished?.invoke(
                user.unsafeCast<IAgoraRTCRemoteUser>(),
                mediaType.toString()
            )
        }

        client.on("user-left") { user, _ ->
            onUserLeft?.invoke(
                user.unsafeCast<IAgoraRTCRemoteUser>()
            )
        }
    }

    suspend fun subscribe(
        user: IAgoraRTCRemoteUser,
        mediaType: String
    ) = client?.subscribe(user, mediaType)?.await()

    suspend fun setMicEnabled(enabled: Boolean) {
        microphoneTrack?.setEnabled(enabled)?.await()
    }

    suspend fun setCameraEnabled(enabled: Boolean) {
        cameraTrack?.setEnabled(enabled)?.await()
    }

    suspend fun leave() {

        runCatching { microphoneTrack?.close() }
        runCatching { cameraTrack?.close() }
        runCatching { client?.leave()?.await() }

        microphoneTrack = null
        cameraTrack = null
        client = null
    }
}