package com.teacheronline.pages.secretary.dashboard

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceStatus
import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.Teacher

data class SecretaryDashboardState(
    val tab: Int = 0,
    val students: List<Student> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val levels: List<Level> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val attendanceHistory: List<Attendance> = emptyList(),

    // create-student form
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val parentId: String = "",
    val parentContact: String = "",
    val levelId: Long = 0,
    val selectedSubjectIds: Set<Long> = emptySet(),

    // mark-attendance form
    val attendanceStudentId: Long = 0,
    val attendanceSubjectId: Long = 0,
    val attendanceTeacherId: Long = 0,
    val attendanceStatus: AttendanceStatus = AttendanceStatus.PRESENT,

    // record-payment form
    val paymentStudentId: Long = 0,
    val paymentSubjectId: Long = 0,
    val paymentTeacherId: Long = 0,
    val paymentPrice: String = "",
    val paymentNotes: String = "",

    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
