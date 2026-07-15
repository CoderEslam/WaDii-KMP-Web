package com.teacheronline.pages.teacher.schedule

import cafe.adriel.voyager.core.model.screenModelScope
import com.teacheronline.BaseViewModel
import com.teacheronline.domain.model.ScheduleDto
import com.teacheronline.domain.usecase.ScheduleUseCase
import com.teacheronline.domain.usecase.TeacherUseCase
import com.teacheronline.state.AppState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherScheduleViewModel(
    private val scheduleUseCase: ScheduleUseCase,
    private val teacherUseCase: TeacherUseCase
) : BaseViewModel<TeacherScheduleState, TeacherScheduleEvent>() {

    override val initialState: TeacherScheduleState get() = TeacherScheduleState()

    override val state: StateFlow<TeacherScheduleState> = _state
        .onStart { load() }
        .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), initialState)

    override fun onEvent(event: TeacherScheduleEvent) {
        when (event) {
            TeacherScheduleEvent.Load -> load()
            is TeacherScheduleEvent.SetDay -> updateState { it.copy(dayOfWeek = event.value) }
            is TeacherScheduleEvent.SetStart -> updateState { it.copy(timeSlotStart = event.value) }
            is TeacherScheduleEvent.SetEnd -> updateState { it.copy(timeSlotEnd = event.value) }
            is TeacherScheduleEvent.SetSubject -> updateState { it.copy(subjectId = event.id) }
            TeacherScheduleEvent.Create -> create()
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
                },
                onError = { e, _ -> updateState { it.copy(error = e, isLoading = false) } }
            )
        }
    }

    private fun create() = screenModelScope.launch {
        val s = _state.value
        val teacherId = s.me?.id ?: return@launch
        val dto = ScheduleDto(dayOfWeek = s.dayOfWeek, timeSlotStart = s.timeSlotStart, timeSlotEnd = s.timeSlotEnd, teacherId = teacherId, subjectId = s.subjectId)
        scheduleUseCase.create(dto) { r ->
            r.handelState(
                onLoading = { updateState { it.copy(saving = true) } },
                onSuccess = { data ->
                    val created = data.data
                    updateState { it.copy(saving = false, createdThisSession = if (created != null) listOf(created) + it.createdThisSession else it.createdThisSession) }
                    AppState.toast("Schedule slot created")
                },
                onError = { e, _ -> updateState { it.copy(saving = false, error = e) }; AppState.toast("Failed", true) }
            )
        }
    }
}
