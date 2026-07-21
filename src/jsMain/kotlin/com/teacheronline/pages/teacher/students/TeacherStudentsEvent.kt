package com.teacheronline.pages.teacher.students

sealed class TeacherStudentsEvent {
    object Load : TeacherStudentsEvent()
    data class SelectSubject(val id: Long) : TeacherStudentsEvent()
}
