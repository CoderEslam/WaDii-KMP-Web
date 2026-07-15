package com.teacheronline.pages.admin.levels

import com.teacheronline.domain.model.Level

sealed class LevelsEvent {
    object Load : LevelsEvent()
    data class SetName(val value: String) : LevelsEvent()
    data class StartEdit(val level: Level) : LevelsEvent()
    object CancelEdit : LevelsEvent()
    object Save : LevelsEvent()
}
