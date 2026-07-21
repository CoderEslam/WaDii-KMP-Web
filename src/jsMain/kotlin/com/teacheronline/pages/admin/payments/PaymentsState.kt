package com.teacheronline.pages.admin.payments

import com.teacheronline.domain.model.PaymentStudent
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Teacher

data class PaymentsState(
    val tab: Int = 0,
    val students: List<Student> = emptyList(),
    val teachers: List<Teacher> = emptyList(),

    val extraPrice: String = "",
    val extraNotes: String = "",

    val outPrice: String = "",
    val outNotes: String = "",

    val studentId: Long = 0,
    val teacherId: Long = 0,
    val subjectId: Long = 0,
    val studentPrice: String = "",
    val studentNotes: String = "",
    val lookedUpPayments: List<PaymentStudent> = emptyList(),

    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
