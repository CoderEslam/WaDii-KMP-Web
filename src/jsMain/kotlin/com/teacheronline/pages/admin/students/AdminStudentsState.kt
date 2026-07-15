package com.teacheronline.pages.admin.students

import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject

data class AdminStudentsState(
    val students: List<Student> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val levels: List<Level> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val parentId: String = "",
    val parentContact: String = "",
    val levelId: Long = 0,
    val selectedSubjectIds: Set<Long> = emptySet(),
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
