package com.teacheronline.pages.teacher.schedule

sealed class TeacherScheduleEvent {
    object Load : TeacherScheduleEvent()
    data class SetDay(val value: String) : TeacherScheduleEvent()
    data class SetStart(val value: String) : TeacherScheduleEvent()
    data class SetEnd(val value: String) : TeacherScheduleEvent()
    data class SetSubject(val id: Long) : TeacherScheduleEvent()
    object Create : TeacherScheduleEvent()
}
