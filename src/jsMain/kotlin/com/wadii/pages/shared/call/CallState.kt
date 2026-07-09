package com.wadii.pages.shared.call

import com.wadii.data.livekit.LiveKitClient

enum class CallStatus { CALLING, RINGING, CONNECTING, CONNECTED, ENDED }

data class CallState(
    val status: CallStatus = CallStatus.CONNECTING,
    val isCaller: Boolean = false,
    val withVideo: Boolean = true,
    val remoteUserName: String = "",
    val remoteUserImage: String? = null,
    val micEnabled: Boolean = true,
    val cameraEnabled: Boolean = true,
    val localVideoTrack: LiveKitClient.Track? = null,
    val remoteVideoTrack: LiveKitClient.Track? = null,
    val remoteHasVideo: Boolean = false,
    val error: String = ""
)
