package com.wadii.pages.shared.call

import com.wadii.data.agora.ICameraVideoTrack
import com.wadii.data.agora.IRemoteVideoTrack

enum class CallStatus { CALLING, RINGING, CONNECTING, CONNECTED, ENDED }

data class CallState(
    val status: CallStatus = CallStatus.CONNECTING,
    val isCaller: Boolean = false,
    val withVideo: Boolean = true,
    val remoteUserName: String = "",
    val remoteUserImage: String? = null,
    val micEnabled: Boolean = true,
    val cameraEnabled: Boolean = true,
    val localVideoTrack: ICameraVideoTrack? = null,
    val remoteVideoTrack: IRemoteVideoTrack? = null,
    val remoteHasVideo: Boolean = false,
    val error: String = ""
)
