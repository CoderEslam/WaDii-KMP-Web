package com.teacheronline.pages.admin.payments

sealed class PaymentsEvent {
    object Load : PaymentsEvent()
    data class SetTab(val index: Int) : PaymentsEvent()

    data class SetExtraPrice(val value: String) : PaymentsEvent()
    data class SetExtraNotes(val value: String) : PaymentsEvent()
    object CreateExtra : PaymentsEvent()

    data class SetOutPrice(val value: String) : PaymentsEvent()
    data class SetOutNotes(val value: String) : PaymentsEvent()
    object CreateOut : PaymentsEvent()

    data class SetStudent(val id: Long) : PaymentsEvent()
    data class SetTeacher(val id: Long) : PaymentsEvent()
    data class SetSubject(val id: Long) : PaymentsEvent()
    data class SetStudentPrice(val value: String) : PaymentsEvent()
    data class SetStudentNotes(val value: String) : PaymentsEvent()
    object CreateStudentPayment : PaymentsEvent()
    object LookupStudentPayments : PaymentsEvent()
}
