package com.teacheronline.pages.teacher.payments

import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject
import com.teacheronline.domain.model.Teacher

data class TeacherPaymentsState(
    val me: Teacher? = null,
    val selectedSubjectId: Long = 0,
    val studentsInSubject: List<Student> = emptyList(),
    val selectedStudentId: Long = 0,
    val price: String = "",
    val notes: String = "",
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val mySubjects: List<Subject> get() = me?.subjects ?: emptyList()
}
