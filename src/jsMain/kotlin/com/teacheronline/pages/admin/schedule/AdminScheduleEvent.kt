package com.teacheronline.pages.admin.schedule

sealed class AdminScheduleEvent {
    object Load : AdminScheduleEvent()
    data class SetDay(val value: String) : AdminScheduleEvent()
    data class SetStart(val value: String) : AdminScheduleEvent()
    data class SetEnd(val value: String) : AdminScheduleEvent()
    data class SetTeacher(val id: Long) : AdminScheduleEvent()
    data class SetSubject(val id: Long) : AdminScheduleEvent()
    object Create : AdminScheduleEvent()
}
