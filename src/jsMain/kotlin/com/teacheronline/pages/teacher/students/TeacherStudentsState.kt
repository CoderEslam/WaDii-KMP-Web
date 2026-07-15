package com.teacheronline.pages.teacher.students

import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject

data class TeacherStudentsState(
    val mySubjects: List<Subject> = emptyList(),
    val selectedSubjectId: Long = 0,
    val students: List<Student> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
