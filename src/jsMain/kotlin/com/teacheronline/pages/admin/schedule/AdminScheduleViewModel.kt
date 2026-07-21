package com.teacheronline.pages.admin.schedule

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.domain.usecase.ScheduleUseCase
import com.teacheronline.domain.usecase.SubjectUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminScheduleViewModel(
    private val scheduleUseCase: ScheduleUseCase,
    private val teacherUseCase: TeacherUseCase,
    private val subjectUseCase: SubjectUseCase
) : BaseViewModel<AdminScheduleState, AdminScheduleEvent>() {

    override val initialState: AdminScheduleState get() = AdminScheduleState()

    override val state: StateFlow<AdminScheduleState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: AdminScheduleEvent) {
        when (event) {
            AdminScheduleEvent.Load -> load()
            is AdminScheduleEvent.SetDay -> updateState { it.copy(dayOfWeek = event.value) }
            is AdminScheduleEvent.SetStart -> updateState { it.copy(timeSlotStart = event.value) }
            is AdminScheduleEvent.SetEnd -> updateState { it.copy(timeSlotEnd = event.value) }
            is AdminScheduleEvent.SetTeacher -> updateState { it.copy(teacherId = event.id) }
            is AdminScheduleEvent.SetSubject -> updateState { it.copy(subjectId = event.id) }
            AdminScheduleEvent.Create -> create()
        }
    }

    private fun load() = screenModelScope.launch {
        teacherUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(teachers = data.data ?: emptyList()) } }) }
        subjectUseCase.showAll { r -> r.handelState(onSuccess = { data -> updateState { it.copy(subjects = data.data ?: emptyList()) } }) }
    }

    private fun create() = screenModelScope.launch {
        val s = _state.value
        val dto = ScheduleDto(
            dayOfWeek = s.dayOfWeek, timeSlotStart = s.timeSlotStart, timeSlotEnd = s.timeSlotEnd,
            teacherId = s.teacherId, subjectId = s.subjectId
        )
        scheduleUseCase.create(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { data ->
                    val created = data.data
                    updateState {
                        it.copy(
                            saving = false,
                            createdThisSession = if (created != null) listOf(created) + it.createdThisSession else it.createdThisSession
                        )
                    }
                    AppState.toast("Schedule slot created")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Create failed", true) }
            )
        }
    }
}
