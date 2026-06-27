package com.wadii.pages.shared.profile

import org.w3c.files.File

sealed class ProfileEvent {
    object Load : ProfileEvent()
    data class UploadAvatar(val file: File) : ProfileEvent()
    data class UploadBackground(val file: File) : ProfileEvent()
}
