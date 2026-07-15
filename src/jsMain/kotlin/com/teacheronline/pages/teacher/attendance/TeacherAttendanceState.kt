package com.teacheronline.pages.teacher.attendance

import com.teacheronline.domain.model.Attendance
import com.teacheronline.domain.model.AttendanceStatus
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.Teacher

data class TeacherAttendanceState(
    val me: Teacher? = null,
    val selectedSubjectId: Long = 0,
    val studentsInSubject: List<Student> = emptyList(),
    val selectedStudentId: Long = 0,
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val history: List<Attendance> = emptyList(),
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val mySubjects: List<Subject> get() = me?.subjects ?: emptyList()
}
