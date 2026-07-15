package com.teacheronline.pages.admin.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.usecase.EducationalCenterUseCase
import com.teacheronline.domain.usecase.StudentUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminDashboardScreenModel(
    private val centerUseCase: EducationalCenterUseCase,
    private val teacherUseCase: TeacherUseCase,
    private val subjectUseCase: SubjectUseCase,
    private val studentUseCase: StudentUseCase
) : BaseViewModel<AdminState, AdminDashboardEvent>() {

    override val initialState: AdminState get() = AdminState()

    override val state: StateFlow<AdminState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AdminDashboardEvent) {}

    private fun load() = screenModelScope.launch {
        updateState { it.copy(isLoading = true) }
        centerUseCase.showAll { r ->
            r.handelState(onSuccess = { data ->
                updateState { it.copy(centersCount = data.data?.size ?: 0) }
            }, onError = { e, _ -> updateState { it.copy(message = e) } })
        }
        teacherUseCase.showAll { r ->
            r.handelState(onSuccess = { data ->
                updateState { it.copy(teachersCount = data.data?.size ?: 0) }
            }, onError = { e, _ -> updateState { it.copy(message = e) } })
        }
        subjectUseCase.showAll { r ->
            r.handelState(onSuccess = { data ->
                updateState { it.copy(subjectsCount = data.data?.size ?: 0) }
            }, onError = { e, _ -> updateState { it.copy(message = e) } })
        }
        studentUseCase.all { r ->
            r.handelState(onSuccess = { data ->
                updateState { it.copy(studentsCount = data.data?.size ?: 0, isLoading = false) }
            }, onError = { e, _ -> updateState { it.copy(message = e, isLoading = false) } })
        }
    }
}
