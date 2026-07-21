package com.teacheronline.pages.teacher.attendance

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.AttendanceRequest
import com.teacheronline.domain.usecase.AttendanceUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherAttendanceViewModel(
    private val attendanceUseCase: AttendanceUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<TeacherAttendanceState, TeacherAttendanceEvent>() {

    override val initialState: TeacherAttendanceState get() = TeacherAttendanceState()

    override val state: StateFlow<TeacherAttendanceState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherAttendanceEvent) {
        when (event) {
            TeacherAttendanceEvent.Load -> load()
            is TeacherAttendanceEvent.SelectSubject -> updateState {
                it.copy(selectedSubjectId = event.id, studentsInSubject = it.me?.subjects?.find { s -> s.id == event.id }?.students ?: emptyList())
            }
            is TeacherAttendanceEvent.SelectStudent -> updateState { it.copy(selectedStudentId = event.id) }
            is TeacherAttendanceEvent.SetStatus -> updateState { it.copy(status = event.status) }
            TeacherAttendanceEvent.Submit -> submit()
        }
    }

    private fun load() = screenModelScope.launch {
        val myUserId = AppState.user?.id
        teacherUseCase.showAll { r ->
            r.handelState(
                onLoading = { updateState { it.copy(isLoading = true) } },
                onSuccess = { data ->
                    val me = data.data?.find { it.user.id == myUserId }
                    updateState { it.copy(me = me, isLoading = false) }
                    me?.subjects?.firstOrNull()?.let { onEvent(TeacherAttendanceEvent.SelectSubject(it.id)) }
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
        attendanceUseCase.all { r ->
            r.handelState(onSuccess = { data -> updateState { it.copy(history = data.data ?: emptyList()) } })
        }
    }

    private fun submit() = screenModelScope.launch {
        val s = _state.value
        val teacherId = s.me?.id ?: return@launch
        if (s.selectedStudentId == 0L || s.selectedSubjectId == 0L) return@launch
        val request = AttendanceRequest(status = s.status, studentId = s.selectedStudentId, subjectId = s.selectedSubjectId, teacherId = teacherId)
        attendanceUseCase.create(request) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { data ->
                    val created = data.data
                    updateState { it.copy(saving = false, history = if (created != null) listOf(created) + it.history else it.history) }
                    AppState.toast("Attendance recorded")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }
}
