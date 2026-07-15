package com.teacheronline.pages.admin.config

sealed class ConfigEvent {
    data class SetKey(val value: String) : ConfigEvent()
    object Load : ConfigEvent()
    data class SetNewValue(val value: String) : ConfigEvent()
    object Save : ConfigEvent()
}
