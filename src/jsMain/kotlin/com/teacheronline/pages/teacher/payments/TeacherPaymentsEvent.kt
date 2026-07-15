package com.teacheronline.pages.teacher.payments

sealed class TeacherPaymentsEvent {
    object Load : TeacherPaymentsEvent()
    data class SelectSubject(val id: Long) : TeacherPaymentsEvent()
    data class SelectStudent(val id: Long) : TeacherPaymentsEvent()
    data class SetPrice(val value: String) : TeacherPaymentsEvent()
    data class SetNotes(val value: String) : TeacherPaymentsEvent()
    object Submit : TeacherPaymentsEvent()
}
