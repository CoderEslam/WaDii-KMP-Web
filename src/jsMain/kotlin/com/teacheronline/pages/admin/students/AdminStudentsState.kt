package com.teacheronline.pages.admin.students

import com.teacheronline.domain.model.Level
import com.teacheronline.domain.model.Parent
import com.teacheronline.domain.model.Student
import com.teacheronline.domain.model.Subject

enum class EnrollMode { NEW_PARENT, EXISTING_PARENT }

data class AdminStudentsState(
    val students: List<Student> = emptyList(),
    val parents: List<Parent> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val levels: List<Level> = emptyList(),
    val enrollMode: EnrollMode = EnrollMode.NEW_PARENT,
    val selectedParentId: Long = 0,
    // parent fields — only used when enrollMode == NEW_PARENT
    val parentFirstName: String = "",
    val parentLastName: String = "",
    val parentEmail: String = "",
    val parentPassword: String = "",
    val parentPhone: String = "",
    val parentContact: String = "",
    // student fields
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val levelId: Long = 0,
    val selectedSubjectIds: Set<Long> = emptySet(),
    val saving: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
