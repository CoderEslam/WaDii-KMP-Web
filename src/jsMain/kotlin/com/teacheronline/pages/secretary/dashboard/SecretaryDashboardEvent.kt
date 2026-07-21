package com.teacheronline.pages.secretary.dashboard

import com.teacheronline.domain.model.AttendanceStatus

sealed class SecretaryDashboardEvent {
    object Load : SecretaryDashboardEvent()
    data class SetTab(val index: Int) : SecretaryDashboardEvent()

    data class SetFirstName(val value: String) : SecretaryDashboardEvent()
    data class SetLastName(val value: String) : SecretaryDashboardEvent()
    data class SetEmail(val value: String) : SecretaryDashboardEvent()
    data class SetPassword(val value: String) : SecretaryDashboardEvent()
    data class SetPhone(val value: String) : SecretaryDashboardEvent()
    data class SetParentId(val value: String) : SecretaryDashboardEvent()
    data class SetParentContact(val value: String) : SecretaryDashboardEvent()
    data class SetLevel(val id: Long) : SecretaryDashboardEvent()
    data class ToggleSubject(val id: Long) : SecretaryDashboardEvent()
    object CreateStudent : SecretaryDashboardEvent()
    data class DeleteStudent(val id: Long) : SecretaryDashboardEvent()

    data class SetAttendanceStudent(val id: Long) : SecretaryDashboardEvent()
    data class SetAttendanceSubject(val id: Long) : SecretaryDashboardEvent()
    data class SetAttendanceTeacher(val id: Long) : SecretaryDashboardEvent()
    data class SetAttendanceStatus(val status: AttendanceStatus) : SecretaryDashboardEvent()
    object SubmitAttendance : SecretaryDashboardEvent()

    data class SetPaymentStudent(val id: Long) : SecretaryDashboardEvent()
    data class SetPaymentSubject(val id: Long) : SecretaryDashboardEvent()
    data class SetPaymentTeacher(val id: Long) : SecretaryDashboardEvent()
    data class SetPaymentPrice(val value: String) : SecretaryDashboardEvent()
    data class SetPaymentNotes(val value: String) : SecretaryDashboardEvent()
    object SubmitPayment : SecretaryDashboardEvent()
}
