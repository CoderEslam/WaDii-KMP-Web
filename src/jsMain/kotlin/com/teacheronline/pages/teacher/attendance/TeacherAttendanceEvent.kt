package com.teacheronline.pages.teacher.attendance

import com.teacheronline.domain.model.AttendanceStatus

sealed class TeacherAttendanceEvent {
    object Load : TeacherAttendanceEvent()
    data class SelectSubject(val id: Long) : TeacherAttendanceEvent()
    data class SelectStudent(val id: Long) : TeacherAttendanceEvent()
    data class SetStatus(val status: AttendanceStatus) : TeacherAttendanceEvent()
    object Submit : TeacherAttendanceEvent()
}
